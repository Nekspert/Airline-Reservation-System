package com.myairline.airline_reservation.service;

import com.myairline.airline_reservation.dao.BookingDAO;
import com.myairline.airline_reservation.dao.PaymentDAO;
import com.myairline.airline_reservation.model.Booking;
import com.myairline.airline_reservation.model.Flight;
import com.myairline.airline_reservation.model.Ticket;
import com.myairline.airline_reservation.model.tariff.Tariff;
import com.myairline.airline_reservation.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class BookingService {
    private final BookingDAO bookingDao;
    private final PaymentDAO paymentDao;

    public BookingService(BookingDAO bookingDao, PaymentDAO paymentDao) {
        this.bookingDao = bookingDao;
        this.paymentDao = paymentDao;
    }


    public Booking createBooking(User passenger, Flight flight, Tariff tariff, PaymentService paymentService) {
        Booking b = new Booking();
        b.setPassenger(passenger);
        b.setFlight(flight);
        b.setBookedAt(LocalDateTime.now());

        Ticket tk = new Ticket();
        tk.setPassenger(passenger);
        tk.setFlight(flight);
        tk.setTariff(tariff);
        tk.setPrice(tariff.getBasePrice());
        tk.setBooking(b);
        b.getTickets().add(tk);

        Booking saved = bookingDao.save(b);

        paymentService.recordDebit(saved, passenger);
        return saved;
    }

    public List<Booking> getFor(User u) {
        return bookingDao.findByPassenger(u.getUsername());
    }

    public void deleteBooking(Booking b) {
        paymentDao.deleteByBooking(b);
        bookingDao.delete(b);
    }
}
