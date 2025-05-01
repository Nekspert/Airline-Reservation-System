package com.myairline.airline_reservation.app;

import com.myairline.airline_reservation.dao.FlightDAO;
import com.myairline.airline_reservation.dao.RouteDAO;
import com.myairline.airline_reservation.dao.TariffDAO;
import com.myairline.airline_reservation.dao.UserDAO;
import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.init.DataInitializer;
import com.myairline.airline_reservation.service.FlightService;
import com.myairline.airline_reservation.service.RouteService;
import com.myairline.airline_reservation.service.TariffService;
import com.myairline.airline_reservation.service.UserService;
import com.myairline.airline_reservation.ui.*;
import com.myairline.airline_reservation.utils.JPAUtil;
import jakarta.persistence.EntityManagerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {
    private static MainApp instance;
    private Stage primaryStage;

    private UserService userService;
    private FlightService flightService;
    private RouteService routeService;
    private TariffService tariffService;
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
    }


    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        showLoginWindow();
    }

    /**
     * Показываем экран логина
     */
    private void showLoginWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
        this.controllerLoader(loader);
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects
                .requireNonNull(getClass().getResource("/css/app.css"))
                .toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Вход в систему");
        primaryStage.show();
    }

    /**
     * Переключение на main.fxml после логина
     */
    public void showMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
        this.controllerLoader(loader);
        Scene scene = new Scene(loader.load(), 1200, 800);
        scene.getStylesheets().add(Objects
                .requireNonNull(getClass().getResource("/css/app.css"))
                .toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Airline Reservations");
        primaryStage.centerOnScreen();
    }

    private void controllerLoader(FXMLLoader loader) {
        loader.setControllerFactory(clazz -> {
            if (clazz == LoginController.class) {
                return new LoginController(userService, session);
            }
            if (clazz == RegisterController.class) {
                return new RegisterController(userService);
            }
            if (clazz == MainController.class) {
                return new MainController(flightService, routeService, tariffService, userService);
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
}
