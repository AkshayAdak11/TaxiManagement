package com.taxifleet.patterns;

import com.taxifleet.db.StoredBooking;

public interface TaxiObserver {
    boolean processBookinAllTaxis(StoredBooking booking);
    boolean processBookinNearByTaxis(StoredBooking booking);
}