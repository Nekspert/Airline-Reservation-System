package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.Booking;
import com.myairline.airline_reservation.model.Flight;
import com.myairline.airline_reservation.model.tariff.Tariff;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.List;

public class BookingController {
    @FXML
    private HBox adminFilterBox;
    @FXML
    private ComboBox<User> userCombo;
    @FXML
    private HBox purchaseBox;
    @FXML
    private ComboBox<Flight> flightCombo;
    @FXML
    private ComboBox<Tariff> tariffCombo;
    @FXML
    private TableView<Booking> bookingTable;
    @FXML
    private TableColumn<Booking, Long> colBId;
    @FXML
    private TableColumn<Booking, Flight> colBFlight;
    @FXML
    private TableColumn<Booking, String> colBTickets;
    @FXML
    private TableColumn<Booking, String> colBDate;
    @FXML
    private TableColumn<Booking, Void> colBAction;

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final FlightService flightService;
    private final TariffService tariffService;
    private final UserService userService;

    public BookingController(BookingService bs,
                             FlightService fs,
                             UserService us,
                             TariffService tariffService,
                             PaymentService paymentService) {
        this.bookingService = bs;
        this.flightService = fs;
        this.userService = us;
        this.tariffService = tariffService;
        this.paymentService = paymentService;
    }

    @FXML
    public void initialize() {
        User current = AppSession.get().getCurrentUser();
        boolean isAdmin = current.isAdmin();

        adminFilterBox.setVisible(isAdmin);
        purchaseBox.setVisible(true);

        flightCombo.getItems().setAll(flightService.getAll());
        tariffCombo.getItems().setAll(tariffService.getAll());


        if (isAdmin) {
            userCombo.getItems().setAll(userService.findAll());
            userCombo.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((o, a, b) -> refresh());
            userCombo.getSelectionModel().selectFirst();
        }

        colBId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colBFlight.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFlight()));
        colBTickets.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getTickets().size() + " шт."
        ));
        colBDate.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getBookedAt().toString()
        ));
        colBAction.setCellFactory(makeActionCellFactory());

        refresh();
    }

    @FXML
    public void onBook() {
        Flight f = flightCombo.getValue();
        Tariff t = tariffCombo.getValue();
        if (f == null || t == null) {
            new Alert(Alert.AlertType.WARNING, "Выберите рейс и тариф").showAndWait();
            return;
        }
        User current = AppSession.get().getCurrentUser();
        if (t.getBasePrice().compareTo(current.getBalance()) > 0) {
            new Alert(Alert.AlertType.WARNING, "Вашего баланса не достаточно для покупки").showAndWait();
            return;
        }
        // если админ — берём пассажира из комбобокса, иначе себя
        User passenger = current.isAdmin() ? userCombo.getValue() : current;

        // создаём бронь (с одним билетом внутри) для выбранного пассажира
        bookingService.createBooking(passenger, f, t, paymentService);
        refresh();
    }


    private Callback<TableColumn<Booking, Void>, TableCell<Booking, Void>> makeActionCellFactory() {
        return col -> new TableCell<>() {
            private final Button btn = new Button("Удалить");

            {
                btn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    bookingService.deleteBooking(b);
                    refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
    }

    private void refresh() {
        User c = AppSession.get().getCurrentUser();
        List<Booking> data = bookingService.getFor(
                c.isAdmin() ? userCombo.getValue() : c
        );
        bookingTable.getItems().setAll(data);
    }
}
