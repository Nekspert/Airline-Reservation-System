package com.myairline.airline_reservation.service.pricing;

import com.myairline.airline_reservation.model.Flight;

public class PricingEngine {
    private PricingStrategy strategy;
    public void setPricingStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    public double price(Flight flight) {
        return strategy.calculate(flight);
    }
}
