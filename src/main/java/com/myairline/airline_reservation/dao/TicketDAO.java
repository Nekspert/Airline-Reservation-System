package com.myairline.airline_reservation.dao;

import com.myairline.airline_reservation.model.Ticket;
import com.myairline.airline_reservation.model.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class TicketDAO {
    private final EntityManager em;

    public TicketDAO(EntityManager em) {
        this.em = em;
    }

    public Ticket save(Ticket ticket) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Ticket persisted = em.merge(ticket);
        tx.commit();
        em.clear();
        return persisted;
    }

    public List<Ticket> findByPassenger(User user) {
        return em.createQuery("SELECT t FROM Ticket t WHERE t.passenger.username = :username", Ticket.class)
                .setParameter("username", user.getUsername())
                .getResultList();
    }


    public List<Ticket> findAll() {
        return em.createQuery("SELECT t FROM Ticket t", Ticket.class)
                .getResultList();
    }

    public void delete(Ticket ticket) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(em.contains(ticket) ? ticket : em.merge(ticket));
        tx.commit();
    }
}
