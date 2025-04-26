package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.model.Route;
import com.myairline.airline_reservation.service.RouteService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class RouteController {
    @FXML
    private TextField originField, destinationField, durationField;
    @FXML
    private ListView<Route> routeList;
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @FXML
    public void initialize() {
        routeList.getItems().setAll(routeService.getAll());
    }

    @FXML
    public void onAddRoute() {
        String o = originField.getText(), d = destinationField.getText();
        long mins = Long.parseLong(durationField.getText());
        Route r = routeService.addRoute(new Route(o, d, java.time.Duration.ofMinutes(mins)));
        routeList.getItems().add(r);
        originField.clear();
        destinationField.clear();
        durationField.clear();
    }
}
