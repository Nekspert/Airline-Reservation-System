package com.myairline.airline_reservation.dao;

import com.myairline.airline_reservation.model.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.util.List;

public class UserDAO {
    private final EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    // C – сохранить нового, если уже есть id — merge
    public User save(User user) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        if (user.getId() == null) {
            em.persist(user);
        }
        else {
            em.merge(user);
        }
        tx.commit();
        return user;
    }

    // R - найти по id
    public User findById(Long id) {
        return em.find(User.class, id);
    }

    // R – найти по username
    public User findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class)
                    .setParameter("u", username).getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public void deleteById(Long id) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        User u = em.find(User.class, id);
        if (u != null) {
            em.remove(u);
        }
        tx.commit();
    }
}
