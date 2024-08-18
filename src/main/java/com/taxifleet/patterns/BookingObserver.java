package com.taxifleet.patterns;

import com.taxifleet.model.BookingTaxis;

public interface BookingObserver {
    boolean proccessBooking(BookingTaxis bookingTaxis);
}