package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.observer.TaxiObserver;

import java.util.List;

public interface TaxiService {
    List<StoredTaxi> getAllTaxis();
    StoredTaxi getTaxi(String taxiNumber);
    StoredTaxi createTaxi(StoredTaxi taxi);
    boolean bookTaxi(StoredTaxi taxi, StoredBooking storedBooking);
    StoredTaxi updateTaxi(StoredTaxi taxi);
    void deleteTaxi(String taxiNumber);
    void updateTaxiAvailability(String taxiNumber, boolean available, TaxiStatus taxiStatus);
    List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius);

    boolean unsubscribeTaxi(String taxiNumber);

    void notifyObservers(StoredBooking storedBooking);
    List<StoredBooking> getAllBookingsForTaxiByPreference(String taxiId);

    TaxiObserver getTaxiObserver(String taxiNumber);

    List<TaxiObserver> getAllTaxiObserver();
}