package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.model.Flight;
import com.myairline.airline_reservation.service.FlightService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PassengerFlightController {

    @FXML private TableView<Flight> flightsTable;
    @FXML private TableColumn<Flight,String> colNum;
    @FXML private TableColumn<Flight,String> colRoute;
    @FXML private TableColumn<Flight,String> colDep;
    @FXML private TableColumn<Flight,String> colArr;

    private final FlightService flightService;

    public PassengerFlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @FXML
    public void initialize() {
        colNum  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFlightNumber()));
        colRoute.setCellValueFactory(c -> {
            var r = c.getValue().getRoute();
            return new SimpleStringProperty(r == null ? "" : r.toString());
        });
        colDep  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartureTime().toString()));
        colArr  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getArrivalTime().toString()));

        flightsTable.getItems().setAll(flightService.getAll());
    }
}
