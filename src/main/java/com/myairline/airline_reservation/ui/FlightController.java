package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.model.Flight;
import com.myairline.airline_reservation.model.Route;
import com.myairline.airline_reservation.service.FlightService;
import com.myairline.airline_reservation.service.RouteService;
import com.myairline.airline_reservation.service.pricing.BaseFareStrategy;
import com.myairline.airline_reservation.service.pricing.DiscountStrategy;
import com.myairline.airline_reservation.service.pricing.PricingEngine;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class FlightController {

    @FXML private TextField flightNumberField;
    @FXML private DatePicker departureDatePicker;
    @FXML private ComboBox<Route> routeCombo;
    @FXML private ComboBox<String> strategyCombo;
    @FXML private TableView<Flight> flightsTable;
    @FXML private Label statusLabel;

    private final FlightService flightService;
    private final RouteService routeService;
    private final PricingEngine pricingEngine = new PricingEngine();

    public FlightController(FlightService flightService, RouteService routeService) {
        this.flightService = flightService;
        this.routeService = routeService;
    }

    @SuppressWarnings("unchecked")
    @FXML
    public void initialize() {
        // Заполняем выпадающие списки
        routeCombo.getItems().setAll(routeService.getAll());
        strategyCombo.getItems().setAll("Базовый тариф", "Скидка 10%");
        strategyCombo.getSelectionModel().selectFirst();

        // Явно приводим колонки к TableColumn<Flight,String>
        TableColumn<Flight, String> colNum   = (TableColumn<Flight, String>) flightsTable.getColumns().get(0);
        TableColumn<Flight, String> colDep   = (TableColumn<Flight, String>) flightsTable.getColumns().get(1);
        TableColumn<Flight, String> colArr   = (TableColumn<Flight, String>) flightsTable.getColumns().get(2);
        TableColumn<Flight, String> colRoute = (TableColumn<Flight, String>) flightsTable.getColumns().get(3);
        TableColumn<Flight, String> colPrice = (TableColumn<Flight, String>) flightsTable.getColumns().get(4);

        // Привязываем лямбдами
        colNum.setCellValueFactory(cdf ->
                new SimpleStringProperty(cdf.getValue().getFlightNumber())
        );
        colDep.setCellValueFactory(cdf ->
                new SimpleStringProperty(cdf.getValue().getDepartureTime().toString())
        );
        colArr.setCellValueFactory(cdf ->
                new SimpleStringProperty(cdf.getValue().getArrivalTime().toString())
        );
        colRoute.setCellValueFactory(cdf -> {
            Route r = cdf.getValue().getRoute();
            return new SimpleStringProperty(r != null ? r.toString() : "");
        });
        colPrice.setCellValueFactory(cdf -> {
            // всегда считаем базовую стоимость для отображения
            pricingEngine.setPricingStrategy(new BaseFareStrategy());
            double p = pricingEngine.price(cdf.getValue());
            return new SimpleStringProperty(String.format("%.2f", p));
        });

        // И сразу загрузим данные
        onLoadFlights();
    }


    @FXML
    public void onLoadFlights() {
        flightsTable.getItems().setAll(flightService.getAll());
        statusLabel.setText("Загружено рейсов: " + flightsTable.getItems().size());
    }

    @FXML
    public void onAddFlight() {
        try {
            String number = flightNumberField.getText().trim();
            LocalDate depDate = departureDatePicker.getValue();
            Route route = routeCombo.getValue();
            String strategyName = strategyCombo.getValue();

            if (number.isEmpty() || depDate == null || route == null) {
                showAlert("Заполните все поля: номер, дата и маршрут.");
                return;
            }

            // Задаём стратегию
            if ("Скидка 10%".equals(strategyName)) {
                pricingEngine.setPricingStrategy(new DiscountStrategy());
            } else {
                pricingEngine.setPricingStrategy(new BaseFareStrategy());
            }

            // Создание и сохранение рейса
            LocalDateTime depTime = depDate.atTime(10, 0); // временно фиксированное время
            LocalDateTime arrTime = depTime.plusHours(2); // прибытие через 2 часа
            Flight flight = new Flight(number, depTime, arrTime);
            flight.setRoute(route);
            flightService.addFlight(flight);

            onLoadFlights();
            statusLabel.setText("Рейс добавлен: " + number);

        } catch (Exception e) {
            showAlert("Ошибка добавления: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
