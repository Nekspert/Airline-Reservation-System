package com.myairline.airline_reservation.service;

import com.myairline.airline_reservation.dao.TicketDAO;
import com.myairline.airline_reservation.model.Flight;
import com.myairline.airline_reservation.model.Ticket;
import com.myairline.airline_reservation.model.tariff.Tariff;
import com.myairline.airline_reservation.model.user.User;

import java.math.BigDecimal;
import java.util.List;

public class TicketService {
    private final TicketDAO dao;

    public TicketService(TicketDAO dao) {
        this.dao = dao;
    }

    public Ticket createTicket(User passenger, Flight flight, Tariff tariff, BigDecimal price) {
        Ticket tk = new Ticket();
        tk.setPassenger(passenger);
        tk.setFlight(flight);
        tk.setTariff(tariff);
        tk.setPrice(price);
        return dao.save(tk);
    }

    public List<Ticket> getFor(User u) {
        if (u.isAdmin()) {
            return dao.findAll();
        } else {
            return dao.findByPassenger(u);
        }
    }

    public void deleteTicket(Ticket t) {
        dao.delete(t);
    }
}
