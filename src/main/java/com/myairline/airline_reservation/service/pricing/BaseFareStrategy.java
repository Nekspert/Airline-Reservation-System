package com.myairline.airline_reservation.service.pricing;

import com.myairline.airline_reservation.model.Flight;

public class BaseFareStrategy implements PricingStrategy {
    @Override
    public double calculate(Flight flight) {
        if (flight.getRoute() == null) {
            throw new IllegalArgumentException("Flight route is not set for flight " + flight.getFlightNumber());
        }
        // например, 5 € за минуту полёта
        return flight.getRoute().getDurationMinutes() * 5.0;
    }
}
