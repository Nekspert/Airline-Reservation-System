package com.myairline.airline_reservation.service;

import com.myairline.airline_reservation.dao.BookingDAO;
import com.myairline.airline_reservation.model.Booking;
import com.myairline.airline_reservation.model.Flight;
import com.myairline.airline_reservation.model.Ticket;
import com.myairline.airline_reservation.model.tariff.Tariff;
import com.myairline.airline_reservation.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class BookingService {
    private final BookingDAO bookingDao;

    public BookingService(BookingDAO bookingDao) {
        this.bookingDao = bookingDao;
    }


    public Booking createBooking(User passenger, Flight flight, Tariff tariff) {
        Booking b = new Booking();
        b.setPassenger(passenger);
        b.setFlight(flight);
        b.setBookedAt(LocalDateTime.now());

        // готовим билет
        Ticket tk = new Ticket();
        tk.setPassenger(passenger);
        tk.setFlight(flight);
        tk.setTariff(tariff);
        tk.setPrice(tariff.getBasePrice());
        // **привязываем** его к броне
        tk.setBooking(b);
        b.getTickets().add(tk);

        // При сохранении брони благодаря cascade=ALL будет INSERT и в tickets
        return bookingDao.save(b);
    }

    public List<Booking> getFor(User u) {
        return u.isAdmin() ? bookingDao.findAll() : bookingDao.findByPassenger(u.getUsername());
    }

    public void deleteBooking(Booking b) {
        bookingDao.delete(b);
    }
}
