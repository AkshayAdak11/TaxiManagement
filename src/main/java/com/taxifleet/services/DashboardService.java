package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.repository.BookingRepository;
import io.dropwizard.hibernate.UnitOfWork;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

public class DashboardService {

    private final BookingRepository bookingRepository;

    @Inject
    public DashboardService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @UnitOfWork
    public long getTotalBookings() {
        return bookingRepository.getAllBookings().size();
    }

    @UnitOfWork
    public long getTotalCompletedBookings() {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> "completed".equals(booking.getStatus()))
                .count();
    }

    @UnitOfWork
    public long getTotalPendingBookings() {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> "pending".equals(booking.getStatus()))
                .count();
    }

    @UnitOfWork
    public List<StoredBooking> getAllBookings() {
        return bookingRepository.getAllBookings();
    }
}