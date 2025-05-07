package com.myairline.airline_reservation.dao;

import com.myairline.airline_reservation.model.Booking;
import com.myairline.airline_reservation.model.Payment;
import com.myairline.airline_reservation.model.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class PaymentDAO {
    private final EntityManager em;

    public PaymentDAO(EntityManager em) {
        this.em = em;
    }

    public Payment save(Payment p) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(p);
        tx.commit();
        return p;
    }

    public List<Payment> findByUser(User u) {
        return em.createQuery(
                        "SELECT p FROM Payment p WHERE p.user = :u ORDER BY p.paidAt ASC, p.id ASC", Payment.class
                )
                .setParameter("u", u)
                .getResultList();
    }

    public List<Payment> findAll() {
        return em.createQuery("SELECT p FROM Payment p ORDER BY p.id ASC", Payment.class).getResultList();
    }

    public void delete(Payment p) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(em.contains(p) ? p : em.merge(p));
        tx.commit();
    }

    public void deleteByBooking(Booking b) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.createQuery("DELETE FROM Payment p WHERE p.booking = :b")
                .setParameter("b", b)
                .executeUpdate();
        tx.commit();
    }

    /** Помечает платеж как PAID и сохраняет время */
    public Payment confirm(Payment p) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        if (p.getId() == null) em.persist(p);
        else em.merge(p);
        p.setAt(java.time.LocalDateTime.now());
        tx.commit();
        return p;
    }
}
