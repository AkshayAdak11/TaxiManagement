package com.taxifleet.repository;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.model.Location;

import java.util.List;

public interface BookingRepository {
    StoredBooking createBooking(StoredBooking booking);
    StoredBooking getBooking(long bookingId);
    List<StoredBooking> getAllBookings();
    StoredBooking updateBooking(StoredBooking booking);
    void deleteBooking(Long bookingId);
    List<StoredBooking> findBookingsByLocation(Location location);

    List<StoredBooking> findAllPendingBookings();
}