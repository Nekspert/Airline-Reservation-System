package com.myairline.airline_reservation.dao;

import com.myairline.airline_reservation.model.Route;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class RouteDAO {
    private final EntityManager em;

    public RouteDAO(EntityManager em) {
        this.em = em;
    }

    public Route save(Route route) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(route);
        tx.commit();
        return route;
    }

    public List<Route> findAll() {
        return em.createQuery("SELECT r FROM Route r", Route.class).getResultList();
    }
}
