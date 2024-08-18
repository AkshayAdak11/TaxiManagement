package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.model.BookingTaxis;

import java.util.List;

public interface BookingService {
    void publishBooking(BookingTaxis bookingTaxis);
    List<StoredBooking> getBookings();
    StoredBooking getBooking(Long id);
    StoredBooking createBooking(BookingTaxis bookingTaxis);
    void deleteBooking(Long id);
    void cancelBooking(BookingTaxis bookingTaxis);
    void confirmBooking(BookingTaxis bookingTaxis, String taxiId);
}