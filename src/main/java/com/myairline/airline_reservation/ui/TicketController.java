package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.Flight;
import com.myairline.airline_reservation.model.Ticket;
import com.myairline.airline_reservation.model.tariff.Tariff;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.FlightService;
import com.myairline.airline_reservation.service.TariffService;
import com.myairline.airline_reservation.service.TicketService;
import com.myairline.airline_reservation.service.UserService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.util.List;

public class TicketController {
    @FXML private HBox    adminFilterBox;
    @FXML private ComboBox<User> userCombo;
    @FXML private HBox    purchaseBox;
    @FXML private ComboBox<Flight> flightCombo;
    @FXML private ComboBox<Tariff> tariffCombo;
    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, Long> colId;
    @FXML private TableColumn<Ticket, Flight> colFlight;
    @FXML private TableColumn<Ticket, Tariff> colTariff;
    @FXML private TableColumn<Ticket, BigDecimal> colPrice;
    @FXML private TableColumn<Ticket, Void> colAction;

    private final TicketService ticketService;
    private final FlightService  flightService;
    private final TariffService  tariffService;
    private final UserService    userService;

    public TicketController(TicketService ts,
                            FlightService fs,
                            TariffService tfs,
                            UserService us) {
        this.ticketService = ts;
        this.flightService = fs;
        this.tariffService = tfs;
        this.userService   = us;
    }

    @FXML
    public void initialize() {
        User current = AppSession.get().getCurrentUser();
        boolean isAdmin = current.isAdmin();

        // показываем\скрываем панели
        adminFilterBox.setVisible(isAdmin);
        purchaseBox   .setVisible(!isAdmin);

        // наполняем комбобоксы
        flightCombo.getItems().setAll(flightService.getAll());
        tariffCombo.getItems().setAll(tariffService.getAll());

        if (isAdmin) {
            List<User> users = userService.findAll();
            userCombo.getItems().setAll(users);
            userCombo.getSelectionModel().selectedItemProperty()
                    .addListener((o, a, b) -> refreshTable());
            userCombo.getSelectionModel().selectFirst();
        }

        // инициализация столбцов
        colId     .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colFlight .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFlight()));
        colTariff .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTariff()));
        colPrice  .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPrice()));
        colAction .setCellFactory(makeActionCellFactory());

        // первый рендер
        refreshTable();
    }

    // покупка нового билета
    @FXML
    public void onBuyTicket() {
        Flight f = flightCombo.getValue();
        Tariff t = tariffCombo.getValue();
        if (f == null || t == null) {
            new Alert(Alert.AlertType.WARNING, "Выберите рейс и тариф").showAndWait();
            return;
        }
        User u = AppSession.get().getCurrentUser();
        BigDecimal price = t.getBasePrice();
        ticketService.createTicket(u, f, t, price);
        refreshTable();
    }

    // удаление (админ)
    private Callback<TableColumn<Ticket, Void>, TableCell<Ticket, Void>> makeActionCellFactory() {
        return col -> new TableCell<>() {
            private final Button btn = new Button("Удалить");
            {
                btn.setOnAction(e -> {
                    Ticket t = getTableView().getItems().get(getIndex());
                    ticketService.deleteTicket(t);
                    refreshTable();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
    }

    // обновить данные в таблице
    private void refreshTable() {
        User current = AppSession.get().getCurrentUser();
        List<Ticket> data;
        if (current.isAdmin()) {
            User sel = userCombo.getValue();
            data = ticketService.getFor(sel);
        } else {
            data = ticketService.getFor(current);
        }
        ticketTable.getItems().setAll(data);
    }
}
