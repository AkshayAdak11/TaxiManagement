package com.taxifleet.strategy;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;

public interface BookingAssignmentStrategy {
    boolean assignBooking(StoredTaxi taxi, StoredBooking storedBooking);

    boolean isEligibleToServeBooking(StoredTaxi taxi, StoredBooking storedBooking);
}
