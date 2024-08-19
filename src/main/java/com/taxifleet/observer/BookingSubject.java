package com.taxifleet.observer;

import com.taxifleet.db.StoredBooking;

import java.util.ArrayList;
import java.util.List;

public interface BookingSubject {
    void registerTaxi(TaxiObserver taxi);
    void unregisterTaxi(TaxiObserver taxi);
    void notifyTaxis(StoredBooking booking);
}
