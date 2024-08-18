package com.taxifleet.repository;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.model.Location;

import java.util.List;

public interface BookingRepository {
    StoredBooking createBooking(StoredBooking booking);
    StoredBooking getBooking(Long id);
    List<StoredBooking> getAllBookings();
    StoredBooking updateBooking(StoredBooking booking);
    void deleteBooking(Long id);
    List<StoredBooking> findBookingsByLocation(Location location);
}