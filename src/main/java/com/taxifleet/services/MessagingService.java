package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.strategy.BookingAssignmentStrategy;

public interface MessagingService {

    void notifyTaxis(StoredBooking storedBooking);

}