package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.model.Flight;
import com.myairline.airline_reservation.model.Route;
import com.myairline.airline_reservation.service.FlightService;
import com.myairline.airline_reservation.service.RouteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FlightController {

    @FXML private TextField    flightNumberField;
    @FXML private DatePicker     departureDatePicker;
    @FXML private ComboBox<Route> routeCombo;
    @FXML private TableView<Flight> flightsTable;
    @FXML private Label          statusLabel;

    private final FlightService flightService;
    private final RouteService  routeService;

    public FlightController(FlightService flightService, RouteService routeService) {
        this.flightService = flightService;
        this.routeService  = routeService;
    }

    @SuppressWarnings("unchecked")
    @FXML
    public void initialize() {
        // Заполняем маршруты
        routeCombo.getItems().setAll(routeService.getAll());

        // Настройка колонок
        var cols = flightsTable.getColumns();
        TableColumn<Flight, String> colNum   = (TableColumn<Flight, String>) cols.get(0);
        TableColumn<Flight, String> colDep   = (TableColumn<Flight, String>) cols.get(1);
        TableColumn<Flight, String> colArr   = (TableColumn<Flight, String>) cols.get(2);
        TableColumn<Flight, String> colRoute = (TableColumn<Flight, String>) cols.get(3);

        colNum.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFlightNumber())
        );
        colDep.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDepartureTime().toString())
        );
        colArr.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getArrivalTime().toString())
        );
        colRoute.setCellValueFactory(c -> {
            Route r = c.getValue().getRoute();
            return new SimpleStringProperty(r != null ? r.toString() : "");
        });

        // Загрузка сразу
        onLoadFlights();
    }

    @FXML
    public void onLoadFlights() {
        flightsTable.getItems().setAll(flightService.getAll());
        statusLabel.setText("Загружено рейсов: " + flightsTable.getItems().size());
    }

    @FXML
    public void onAddFlight() {
        String number = flightNumberField.getText().trim();
        LocalDate depDate = departureDatePicker.getValue();
        Route route = routeCombo.getValue();

        if (number.isEmpty() || depDate == null || route == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Заполните все поля: номер, дата и маршрут.")
                    .showAndWait();
            return;
        }

        LocalDateTime depTime = depDate.atTime(10, 0);
        LocalDateTime arrTime = depTime.plusHours(2);
        Flight flight = new Flight(number, depTime, arrTime);
        flight.setRoute(route);
        flightService.addFlight(flight);

        onLoadFlights();
        statusLabel.setText("Рейс добавлен: " + number);
    }
}
