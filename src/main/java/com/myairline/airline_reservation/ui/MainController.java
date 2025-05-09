package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.app.MainApp;
import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    private ListView<String> navList;
    @FXML
    private StackPane contentPane;
    @FXML
    private Button settingsBtn;

    private final FlightService flightService;
    private final RouteService  routeService;
    private final TariffService tariffService;
    private final UserService userService;
    private final TicketService ticketService;
    private final BookingService bookingService;
    private final PaymentService paymentService;

    // Конструктор вызывается из MainApp через controllerFactory
    public MainController(FlightService flightService, RouteService routeService, TariffService tariffService,
                          UserService userService, TicketService ticketService, BookingService bookingService,
                          PaymentService paymentService) {
        this.flightService = flightService;
        this.routeService  = routeService;
        this.tariffService = tariffService;
        this.userService = userService;
        this.ticketService = ticketService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
    }

    @FXML
    public void initialize() {
        User user = AppSession.get().getCurrentUser();
        List<String> items = new ArrayList<>();

        if (user.isAdmin()) {
            items.addAll(List.of(
                    "Пользователи",
                    "Все бронирования",
                    "Все билеты",
                    "Все платежи"
            ));
        } else {
            items.addAll(List.of(
                    "Мои бронирования",
                    "Мои билеты",
                    "Мои платежи"
            ));
        }
        items.add("Рейсы");
        items.add("Маршруты");
        items.add("Тарифы");
        navList.getItems().setAll(items);

        // Слушатель для переключения разделов
        navList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                loadSection(newV);
            }
        });
        // Выбираем первый пункт по умолчанию
        navList.getSelectionModel().selectFirst();
    }

    private void loadSection(String section) {
        contentPane.getChildren().clear();

        String fxml;
        // pick FXML
        if (section.equals("Рейсы")) {
            boolean isAdmin = AppSession.get().getCurrentUser().isAdmin();
            fxml = isAdmin
                    ? "flight_page.fxml"
                    : "passenger_flight_page.fxml";
        } else if (section.equals("Маршруты")) {
            boolean isAdmin = AppSession.get().getCurrentUser().isAdmin();
            fxml = isAdmin
                    ? "route_page.fxml"
                    : "passenger_route_page.fxml";
        } else if (section.equals("Тарифы")) {
            boolean isAdmin = AppSession.get().getCurrentUser().isAdmin();
            fxml = isAdmin
                    ? "tariff_page.fxml"
                    : "passenger_tariff_page.fxml";
        } else {
            fxml = toFxmlName(section);
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/" + fxml));
            loader.setControllerFactory(controllerClass -> {
                // Подавляем главного контроллера, чтобы не рекурсировать
                if (controllerClass == MainController.class) {
                    return this;
                }
                // Секции
                return switch (section) {
                    case "Рейсы" -> {
                        boolean isAdmin = AppSession.get().getCurrentUser().isAdmin();
                        if (isAdmin) yield new FlightController(flightService, routeService);
                        else yield new PassengerFlightController(flightService);
                    }
                    case "Маршруты" -> {
                        boolean isAdmin = AppSession.get().getCurrentUser().isAdmin();
                        if (isAdmin) yield new RouteController(routeService);
                        else yield new PassengerRouteController(routeService);
                    }
                    case "Тарифы" -> {
                        boolean isAdmin = AppSession.get().getCurrentUser().isAdmin();
                        if (isAdmin) yield new TariffController(tariffService);
                        else       yield new PassengerTariffController(tariffService);
                    }
                    case "Пользователи" -> new UserController(userService);
                    case "Все билеты", "Мои билеты" -> new TicketController(ticketService, userService);
                    case "Все бронирования", "Мои бронирования" ->
                            new BookingController(bookingService, flightService, userService, tariffService, paymentService);
                    case "Все платежи", "Мои платежи" -> new PaymentController(paymentService, userService);


                    default -> throw new IllegalStateException("Нет контроллера для раздела " + section);
                };
            });
            Parent view = loader.load();
            contentPane.getChildren().add(view);

        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить раздел " + section, e);
        }
    }

    private String toFxmlName(String section) {
        return switch (section) {
            case "Рейсы" -> "flight_page.fxml";
            case "Маршруты" -> "route_page.fxml";
            case "Тарифы" -> "tariff_page.fxml";
            case "Пользователи" -> "users_page.fxml";
            case "Все билеты",
                 "Мои билеты" -> "ticket_page.fxml";
            case "Все бронирования",
                 "Мои бронирования" -> "booking_page.fxml";
            case "Все платежи", "Мои платежи" -> "payment_page.fxml";

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/settings_page.fxml"));
            MainApp.getInstance().controllerLoader(loader);
            Parent pane = loader.load();

            // Создаем Popup
            Popup popup = new Popup();
            popup.getContent().add(pane);
            popup.setAutoHide(true);

            // Передаем Popup контроллеру, чтобы он мог его закрыть
            SettingsController ctrl = loader.getController();
            ctrl.setPopup(popup);

            // вычисляем ширину и координаты
            double popupWidth = pane.prefWidth(-1);
            if (popupWidth <= 0) popupWidth = pane.getLayoutBounds().getWidth();
            Bounds btnBounds = settingsBtn.localToScreen(settingsBtn.getBoundsInLocal());
            double anchorX = btnBounds.getMaxX() - popupWidth;
            double anchorY = btnBounds.getMaxY();

            Stage owner = MainApp.getInstance().getPrimaryStage();
            popup.show(owner, anchorX, anchorY);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Не удалось открыть панель настроек",
                    ButtonType.OK).showAndWait();
        }
    }

    public void selectSection(String name) {
        navList.getSelectionModel().select(name);
    }
}
