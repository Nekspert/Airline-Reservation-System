package com.myairline.airline_reservation.service.pricing;

import com.myairline.airline_reservation.model.Flight;

public class DiscountStrategy implements PricingStrategy {
    @Override
    public double calculate(Flight flight) {
        if (flight.getRoute() == null) {
            throw new IllegalArgumentException("Flight route is not set for flight " + flight.getFlightNumber());
        }
        return new BaseFareStrategy().calculate(flight) * 0.9; // скидка 10%
    }
}