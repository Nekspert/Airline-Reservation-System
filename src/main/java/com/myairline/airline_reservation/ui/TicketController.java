package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.app.MainApp;
import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.Ticket;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.TicketService;
import com.myairline.airline_reservation.service.UserService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.util.List;

public class TicketController {
    @FXML
    private HBox adminFilterBox;
    @FXML
    private ComboBox<User> userCombo;
    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, Long> colId;
    @FXML
    private TableColumn<Ticket, String> colFlight;
    @FXML
    private TableColumn<Ticket, String> colTariff;
    @FXML
    private TableColumn<Ticket, String> colPrice;

    private final TicketService ticketService;
    private final UserService userService;
    private User current;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        current = AppSession.get().getCurrentUser();
        boolean isAdmin = current.isAdmin();

        adminFilterBox.setVisible(isAdmin);
        if (isAdmin) {
            userCombo.getItems().setAll(userService.findAll());
            userCombo.getSelectionModel().selectedItemProperty()
                    .addListener((obs, oldU, newU) -> refreshTable());
            userCombo.getSelectionModel().selectFirst();
        }

        colId.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));
        colFlight.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getFlight().toString()));
        colTariff.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getTariff().getName()));
        colPrice.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getPrice().toString()));

        refreshTable();
    }

    @FXML
    public void onGoToBooking() {
        MainApp.getInstance().getMainController().selectSection("Мои бронирования");
    }

    private void refreshTable() {
        List<Ticket> list;
        User sel;
        if (current.isAdmin()) {
            sel = userCombo.getValue();
        } else {
            sel = current;
        }
        list = ticketService.getFor(sel);
        ticketTable.getItems().setAll(list);
    }
}
