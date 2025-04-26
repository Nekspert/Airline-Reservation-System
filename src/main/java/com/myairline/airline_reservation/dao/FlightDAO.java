package com.myairline.airline_reservation.dao;

import com.myairline.airline_reservation.model.Flight;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class FlightDAO {
    private final EntityManager em;

    public FlightDAO(EntityManager em) {
        this.em = em;
    }

    public Flight save(Flight flight) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(flight);
        tx.commit();
        return flight;
    }

    public List<Flight> findAll() {
        return em.createQuery("SELECT f FROM Flight f", Flight.class).getResultList();
    }
}
