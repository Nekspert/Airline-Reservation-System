package com.myairline.airline_reservation.service;

import com.myairline.airline_reservation.dao.PaymentDAO;
import com.myairline.airline_reservation.model.Booking;
import com.myairline.airline_reservation.model.Payment;
import com.myairline.airline_reservation.model.Ticket;
import com.myairline.airline_reservation.model.user.User;

import java.math.BigDecimal;
import java.util.List;

public class PaymentService {
    private final PaymentDAO dao;

    public PaymentService(PaymentDAO dao) {
        this.dao = dao;
    }

    public Payment initPayment(Booking b) {
        Payment p = new Payment();
        p.setBooking(b);
        p.setAmount(b.getTickets()
                .stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        p.setStatus(Payment.PaymentStatus.PENDING);
        return dao.save(p);
    }

    /**
     * Для admin: первый аргумент — фильтрующий пользователь, второй — кто смотрит
     */
    public List<Payment> getFor(User filter, User current) {
        if (current.isAdmin()) {
            return filter == null
                    ? dao.findAll()
                    : dao.findByUser(filter);
        } else {
            return dao.findByUser(current);
        }
    }

    public void deletePayment(Payment p) {
        dao.delete(p);
    }

    public void confirmPayment(Payment p) {
        dao.confirm(p);
    }
}
