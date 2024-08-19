package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;

import java.util.List;

public interface BookingService {
    void publishBooking(StoredBooking storedBooking);
    List<com.taxifleet.db.StoredBooking> getBookings();
    com.taxifleet.db.StoredBooking getBooking(Long id);
    com.taxifleet.db.StoredBooking createBooking(StoredBooking storedBooking);
    void deleteBooking(Long id);
    void cancelBooking(StoredBooking storedBooking);
    void confirmBooking(StoredBooking storedBooking, String taxiId);
}