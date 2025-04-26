package com.myairline.airline_reservation.service;

import com.myairline.airline_reservation.dao.RouteDAO;
import com.myairline.airline_reservation.model.Route;
import java.util.List;

public class RouteService {
    private final RouteDAO dao;

    public RouteService(RouteDAO dao) {
        this.dao = dao;
    }

    public Route addRoute(Route route) {
        return dao.save(route);
    }

    public List<Route> getAll() {
        return dao.findAll();
    }
}
