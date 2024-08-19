package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.strategy.BookingAssignmentStrategy;

public interface MessagingService {
    void publishBooking(StoredBooking storedBooking);

    void notifyTaxis(StoredBooking storedBooking);

    BookingAssignmentStrategy createStrategy(BookingStrategy strategy);

}