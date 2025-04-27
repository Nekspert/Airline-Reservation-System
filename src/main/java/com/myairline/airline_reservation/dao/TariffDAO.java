package com.myairline.airline_reservation.dao;

import com.myairline.airline_reservation.model.tariff.Tariff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class TariffDAO {
    private final EntityManager em;

    public TariffDAO(EntityManager em) {
        this.em = em;
    }

    public Tariff save(Tariff tariff) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(tariff);
        tx.commit();
        return tariff;
    }

    public void delete(Tariff tariff) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(tariff);
        tx.commit();
    }

    public List<Tariff> findAll() {
        return em.createQuery("SELECT t FROM Tariff t ", Tariff.class).getResultList();
    }
}
