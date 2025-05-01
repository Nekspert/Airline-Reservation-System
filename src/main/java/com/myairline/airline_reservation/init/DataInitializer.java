package com.myairline.airline_reservation.init;

import com.myairline.airline_reservation.dao.UserDAO;
import com.myairline.airline_reservation.model.user.Role;
import com.myairline.airline_reservation.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DataInitializer {
    public static void seed() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("airlinePU");
        EntityManager em = emf.createEntityManager();
        UserDAO dao = new UserDAO(em);
        UserService service = new UserService(dao);

        // Только если таблица пуста
        if (service.findAll().isEmpty()) {
            service.register("Timur", "1234", Role.ADMIN);
            service.register("Grisha", "0000", Role.PASSENGER);
        }

        em.close();
        emf.close();
    }
}
