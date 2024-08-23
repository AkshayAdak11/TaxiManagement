package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;

import java.util.List;

public interface BookingService {
    void publishBooking(StoredBooking storedBooking);

    List<StoredBooking> getBookings();

    StoredBooking getBooking(long id);

    StoredBooking createBooking(StoredBooking storedBooking);

    void deleteBooking(Long id);

    void cancelBooking(StoredBooking storedBooking);

    void expiredBooking(StoredBooking storedBooking);

    void confirmBooking(StoredBooking storedBooking, String taxiId);

    List<StoredBooking> allPendingBooking();

    List<StoredBooking> getAllBookingsForTaxi(String taxiNumber);
}