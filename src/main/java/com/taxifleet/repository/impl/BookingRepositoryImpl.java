package com.taxifleet.repository.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.dao.BookingDAO;
import com.taxifleet.model.Location;
import com.taxifleet.repository.BookingRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class BookingRepositoryImpl implements BookingRepository {

    private final BookingDAO bookingDAO;

    @Inject
    public BookingRepositoryImpl(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    @Override
    public StoredBooking createBooking(StoredBooking booking) {
        return bookingDAO.create(booking);
    }

    @Override
    public StoredBooking getBooking(Long id) {
        return bookingDAO.findById(id);
    }

    @Override
    public List<StoredBooking> getAllBookings() {
        return bookingDAO.findAll();
    }

    @Override
    public StoredBooking updateBooking(StoredBooking booking) {
        return bookingDAO.create(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        StoredBooking booking = bookingDAO.findById(id);
        if (booking != null) {
            bookingDAO.delete(booking);
        }
    }

    @Override
    public List<StoredBooking> findBookingsByLocation(Location location) {
        return bookingDAO.findAll().stream()
                .filter(booking -> location.getLatitude() == booking.getLatitude())
                .filter(booking -> location.getLongitude() == booking.getLongitude())
                .collect(Collectors.toList());
    }
}