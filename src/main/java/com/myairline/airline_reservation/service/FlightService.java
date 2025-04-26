package com.myairline.airline_reservation.service;

import com.myairline.airline_reservation.model.Flight;
import com.myairline.airline_reservation.dao.FlightDAO;
import java.util.List;

public class FlightService {
    private final FlightDAO dao;

    public FlightService(FlightDAO dao) {
        this.dao = dao;
    }

    public Flight addFlight(Flight f) {
        return dao.save(f);
    }

    public List<Flight> getAll() {
        return dao.findAll();
    }
}
