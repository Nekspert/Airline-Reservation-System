package com.myairline.airline_reservation.model.tariff;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BUSINESS")
public class TariffBusiness extends Tariff {
}