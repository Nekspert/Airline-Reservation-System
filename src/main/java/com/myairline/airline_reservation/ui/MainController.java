package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.service.FlightService;
import com.myairline.airline_reservation.service.RouteService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    @FXML
    private ListView<String> navList;
    @FXML
    private StackPane contentPane;

    private final FlightService flightService;
    private final RouteService  routeService;

    // Конструктор вызывается из MainApp через controllerFactory
    public MainController(FlightService flightService, RouteService routeService) {
        this.flightService = flightService;
        this.routeService  = routeService;
    }

    @FXML
    public void initialize() {
        navList.getItems().setAll(
                "Рейсы", "Маршруты", "Бронирования",
                "Пассажиры", "Билеты", "Платежи"
        );

        navList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            loadSection(newV);
        });

        // Выбираем первый пункт по умолчанию
        navList.getSelectionModel().selectFirst();
    }

    private void loadSection(String section) {
        contentPane.getChildren().clear();
        String fxml = toFxmlName(section);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/" + fxml));
            loader.setControllerFactory(controllerClass -> {
                // Подавляем главного контроллера, чтобы не рекурсировать
                if (controllerClass == MainController.class) {
                    return this;
                }
                // Секции
                return switch (section) {
                    case "Рейсы" -> new FlightController(flightService, routeService);
                    case "Маршруты" -> new RouteController(routeService);

                    // TODO: добавить другие разделы по аналогии:
                    // case "Бронирования": return new BookingController(...);
                    default -> throw new IllegalStateException("Нет контроллера для раздела " + section);
                };
            });
            Parent view = loader.load();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            // на реальном проекте логгируйте и/или показывайте Alert
            throw new RuntimeException("Не удалось загрузить раздел " + section, e);
        }
    }

    private String toFxmlName(String section) {
        return switch (section) {
            case "Рейсы" -> "flight_page.fxml";
            case "Маршруты" -> "route_page.fxml";
            case "Бронирования" -> "booking_page.fxml";
            case "Пассажиры" -> "passenger_page.fxml";
            case "Билеты" -> "ticket_page.fxml";
            case "Платежи" -> "payment_page.fxml";
            default -> throw new IllegalArgumentException("Неизвестный раздел: " + section);
        };
    }

    @FXML
    public void onRefreshAll() {
        // просто перезагружаем текущий раздел
        String current = navList.getSelectionModel().getSelectedItem();
        loadSection(current);
    }

    @FXML
    public void onSettings() {
        // заглушка: покажем диалог с настройками
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Настройки приложения");
        alert.setContentText("Здесь будут ваши настройки.");
        alert.showAndWait();
    }
}
