package com.taxifleet.observer;

import com.taxifleet.db.StoredBooking;

import java.util.List;

public interface Observer {

    void registerObserver(TaxiObserver observer);

    void notifyObservers(StoredBooking storedBooking);

    boolean removeObserver(String taxiNumber);

    List<TaxiObserver> getAllTaxiObserver();

    TaxiObserver getTaxiObserver(String taxiNumber);

    List<StoredBooking> getAllBookingsForTaxi(String taxiNumber);


}
