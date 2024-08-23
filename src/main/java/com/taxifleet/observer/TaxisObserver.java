package com.taxifleet.observer;

import com.taxifleet.db.StoredBooking;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class TaxisObserver extends BaseObserver{

    @Override
    public TaxiManager getTaxiObserver(String taxiNumber) {
        for (TaxiManager observer : taxiManagers) {
            if (observer.getTaxi().getTaxiNumber().equals(taxiNumber)) {
                return observer;
            }
        }
        return null;
    }

    @Override
    public List<StoredBooking> getAllBookingsForTaxi(String taxiNumber) {
        for (TaxiManager observer : taxiManagers) {
            if (observer.getTaxi().getTaxiNumber().equals(taxiNumber)) {
                return new ArrayList<>(observer.getAvailableBookings().values());
            }
        }
        return new ArrayList<>();
    }
}
