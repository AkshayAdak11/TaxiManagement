package com.taxifleet.services;

import com.taxifleet.db.StoredTaxi;
import com.taxifleet.observer.TaxiObserver;

public class TaxiObserverFactory {
    public static void updateTaxiObserver(TaxiObserver observer, StoredTaxi updatedTaxi) {
        if (observer == null || updatedTaxi == null) {
            throw new IllegalArgumentException("Observer and updated taxi cannot be null");
        }

        StoredTaxi taxi = observer.getTaxi();
        if (taxi != null) {
            taxi.setAvailable(updatedTaxi.isAvailable());
            taxi.setStatus(updatedTaxi.getStatus());
            taxi.setVersion(updatedTaxi.getVersion());
        }
    }
}
