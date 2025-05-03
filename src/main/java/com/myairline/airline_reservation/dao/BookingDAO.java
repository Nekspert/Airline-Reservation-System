package com.myairline.airline_reservation.dao;

import com.myairline.airline_reservation.model.Booking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class BookingDAO {
    private final EntityManager em;
    public BookingDAO(EntityManager em) { this.em = em; }

    public Booking save(Booking b) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Booking merged = em.merge(b);
        tx.commit();
        em.clear();
        return merged;
    }

    public List<Booking> findByPassenger(String username) {
        return em.createQuery(
                        "SELECT b FROM Booking b WHERE b.passenger.username = :u", Booking.class)
                .setParameter("u", username)
                .getResultList();
    }

    public List<Booking> findAll() {
        return em.createQuery("SELECT b FROM Booking b", Booking.class)
                .getResultList();
    }

    public void delete(Booking b) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(em.contains(b) ? b : em.merge(b));
        tx.commit();
    }
}
