package com.myairline.airline_reservation.app;

import com.myairline.airline_reservation.dao.FlightDAO;
import com.myairline.airline_reservation.dao.RouteDAO;
import com.myairline.airline_reservation.service.FlightService;
import com.myairline.airline_reservation.service.RouteService;
import com.myairline.airline_reservation.ui.FlightController;
import com.myairline.airline_reservation.ui.MainController;
import com.myairline.airline_reservation.ui.RouteController;
import com.myairline.airline_reservation.utils.JPAUtil;
import jakarta.persistence.EntityManagerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {
    private FlightService flightService;
    private RouteService routeService;

    @Override
    public void init() {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        var em = emf.createEntityManager();
        this.flightService = new FlightService(new FlightDAO(em));
        this.routeService = new RouteService(new RouteDAO(em));
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));

        // Передаём оба сервиса через замыкание
        loader.setControllerFactory(clazz -> {
            if (clazz == MainController.class) {
                return new MainController(flightService, routeService);
            }
            if (clazz == FlightController.class) {
                return new FlightController(flightService, routeService);
            }
            if (clazz == RouteController.class) {
                return new RouteController(routeService);
            }
            throw new IllegalStateException("Unexpected controller " + clazz);
        });

        Scene scene = new Scene(loader.load(), 1200, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/app.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Airline Reservations");
        stage.show();
    }


    @Override
    public void stop() {
        JPAUtil.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }
}
