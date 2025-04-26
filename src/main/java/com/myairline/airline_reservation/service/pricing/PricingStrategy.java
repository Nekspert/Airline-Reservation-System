package com.myairline.airline_reservation.service.pricing;

import com.myairline.airline_reservation.model.Flight;

public interface PricingStrategy {
    double calculate(Flight flight);
}
