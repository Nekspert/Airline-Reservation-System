package com.myairline.airline_reservation.dao;

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

    public List<Payment> findByUser(User user) {
        return em.createQuery(
                        "SELECT p FROM Payment p WHERE p.booking.passenger.username = :user", Payment.class)
                .setParameter("user", user)
                .getResultList();
    }

    public List<Payment> findAll() {
        return em.createQuery("SELECT p FROM Payment p", Payment.class)
                .getResultList();
    }

    public void delete(Payment p) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(em.contains(p) ? p : em.merge(p));
        tx.commit();
    }

    /** Помечает платеж как PAID и сохраняет время */
    public Payment confirm(Payment p) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p = em.merge(p);
        p.setStatus(Payment.PaymentStatus.PAID);
        p.setPaidAt(java.time.LocalDateTime.now());
        tx.commit();
        return p;
    }
}
