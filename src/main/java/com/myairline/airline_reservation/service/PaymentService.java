package com.myairline.airline_reservation.service;

import com.myairline.airline_reservation.dao.PaymentDAO;
import com.myairline.airline_reservation.dao.UserDAO;
import com.myairline.airline_reservation.model.Booking;
import com.myairline.airline_reservation.model.Payment;
import com.myairline.airline_reservation.model.Ticket;
import com.myairline.airline_reservation.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentService {
    private final PaymentDAO dao;
    private final UserDAO userDao;

    public PaymentService(PaymentDAO dao, UserDAO userDao) {
        this.dao = dao;
        this.userDao = userDao;
    }

    public List<Payment> listOperations(User u) {
        return dao.findByUser(u);
    }

    public void recordDebit(Booking b, User u) {
        BigDecimal total = b.getTickets().stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // списываем с баланса
        u.adjustBalance(total.negate());
        userDao.save(u);

        // создаём «трату»
        Payment p = new Payment();
        p.setUser(u);
        p.setBooking(b);
        p.setType(Payment.Type.PURCHASE);
        p.setAmount(total);
        p.setAt(LocalDateTime.now());
        dao.save(p);
    }


    public void recharge(User user, BigDecimal amount) {
        // увеличиваем баланс
        user.adjustBalance(amount);
        userDao.save(user);

        // сохраняем запись о пополнении
        Payment p = new Payment();
        p.setUser(user);
        p.setType(Payment.Type.REPLENISHMENT);
        p.setAmount(amount);
        p.setAt(LocalDateTime.now());
        p.setBooking(null);
        dao.save(p);
    }


    public List<Payment> history(User u) {
        return dao.findByUser(u);
    }

    public void delete(Payment p) {
        dao.delete(p);
    }
}

