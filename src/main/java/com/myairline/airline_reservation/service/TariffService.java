package com.myairline.airline_reservation.service;

import com.myairline.airline_reservation.dao.TariffDAO;
import com.myairline.airline_reservation.model.tariff.Tariff;

import java.util.List;

public class TariffService {
    private final TariffDAO dao;

    public TariffService(TariffDAO dao) {
        this.dao = dao;
    }

    public Tariff addTariff(Tariff tariff) {
        return dao.save(tariff);
    }

    public void removeTariff(Tariff tariff) {
        dao.delete(tariff);
    }

    public List<Tariff> getAll() {
        return dao.findAll();
    }
}
