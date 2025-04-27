package com.myairline.airline_reservation.model.tariff;

public class TariffFactory {
    public static Tariff create(TariffType type) {
        switch (type) {
            case BUSINESS:
                return new TariffBusiness();
            case FLEXIBLE:
                return new TariffFlexible();
            default:
                return new TariffEconomy();
        }
    }
}
