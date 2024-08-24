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
    public StoredBooking getBooking(long id) {
        return bookingDAO.findByBookingId(id);
    }

    @Override
    public List<StoredBooking> getAllBookings() {
        return bookingDAO.findAll();
    }

    @Override
    public StoredBooking updateBooking(StoredBooking booking) {
        return bookingDAO.updateStoredBooking(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        StoredBooking booking = bookingDAO.findByBookingId(id);
        if (booking != null) {
            bookingDAO.deleteStoredBanking(booking);
        }
    }

    @Override
    public List<StoredBooking> findAllPendingBookings() {
        return bookingDAO.findPendingBookings();
        }
}