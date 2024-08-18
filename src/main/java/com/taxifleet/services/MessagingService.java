package com.taxifleet.services;

import com.taxifleet.model.BookingTaxis;

public interface MessagingService {
    void publishBooking(BookingTaxis bookingTaxis);
}