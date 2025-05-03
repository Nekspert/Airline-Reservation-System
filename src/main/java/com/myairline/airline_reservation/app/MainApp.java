package com.myairline.airline_reservation.app;

import com.myairline.airline_reservation.dao.*;
import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.init.DataInitializer;
import com.myairline.airline_reservation.service.*;
import com.myairline.airline_reservation.ui.*;
import com.myairline.airline_reservation.utils.JPAUtil;
import jakarta.persistence.EntityManagerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {
    private static MainApp instance;
    private Stage primaryStage;
    private MainController mainController;

    private UserService userService;
    private FlightService flightService;
    private RouteService routeService;
    private TariffService tariffService;
    private TicketService ticketService;
    private BookingService bookingService;
    private PaymentService paymentService;
    private final AppSession session = AppSession.get();

    public MainApp() {
        instance = this;
    }

    public static MainApp getInstance() {
        return instance;
    }

    @Override
    public void init() {
        // 1) Seed начальных данных: (админ, пассажир, базовые тарифы/маршруты)
        DataInitializer.seed();

        // 2) Создаём EntityManager и сервисы
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        var em = emf.createEntityManager();

        this.userService = new UserService(new UserDAO(em));
        this.flightService = new FlightService(new FlightDAO(em));
        this.routeService = new RouteService(new RouteDAO(em));
        this.tariffService = new TariffService(new TariffDAO(em));
        this.ticketService = new TicketService(new TicketDAO(em));
        this.bookingService = new BookingService(new BookingDAO(em));
        this.paymentService = new PaymentService(new PaymentDAO(em));
    }


    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        showLoginWindow();
    }

    /**
     * Показываем экран логина
     */
    public void showLoginWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
        this.controllerLoader(loader);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects
                .requireNonNull(getClass().getResource("/css/app.css"))
                .toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Вход в систему");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Переключение на main.fxml после логина
     */
    public void showMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
        this.controllerLoader(loader);
        Parent root = loader.load();
        // Сохраняем контроллер именно для main.fxml
        mainController = loader.getController();
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(Objects
                .requireNonNull(getClass().getResource("/css/app.css"))
                .toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Airline Reservations");
        primaryStage.centerOnScreen();
    }

    public void controllerLoader(FXMLLoader loader) {
        loader.setControllerFactory(clazz -> {
            if (clazz == LoginController.class) {
                return new LoginController(userService, session);
            }
            if (clazz == RegisterController.class) {
                return new RegisterController(userService);
            }
            if (clazz == MainController.class) {
                return new MainController(flightService, routeService, tariffService, userService, ticketService,
                        bookingService, paymentService);
            }
            if (clazz == FlightController.class) {
                return new FlightController(flightService, routeService);
            }
            if (clazz == RouteController.class) {
                return new RouteController(routeService);
            }
            if (clazz == TariffController.class) {
                return new TariffController(tariffService);
            }
            if (clazz == UserController.class) {
                return new UserController(userService);
            }
            if (clazz == SettingsController.class) {
                return new SettingsController();
            }
            if (clazz == TicketController.class) {
                return new TicketController(ticketService);
            }
            if (clazz == BookingController.class) {
                return new BookingController(bookingService, flightService, userService, tariffService);
            }
            if (clazz == PaymentController.class) {
                return new PaymentController(paymentService, userService);
            }
            throw new IllegalStateException("Unexpected controller: " + clazz);
        });
    }

    @Override
    public void stop() {
        JPAUtil.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public MainController getMainController() {
        return mainController;
    }
}
