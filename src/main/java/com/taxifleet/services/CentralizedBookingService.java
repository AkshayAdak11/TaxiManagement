package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;

public interface CentralizedBookingService {
    boolean assignBookingToTaxi(StoredTaxi taxi, StoredBooking storedBooking);

    void notifyObserversBookingCompleted(StoredBooking storedBooking);

    void removeBookingFromAssignment(StoredBooking storedBooking);
}