package com.taxifleet.observer;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.services.TaxiManager;

import java.util.List;

public interface Observer {

    void registerObserver(TaxiManager observer);

    void notifyObservers(StoredBooking storedBooking);

    boolean removeObserver(String taxiNumber);

    List<TaxiManager> getAllTaxiManager();

    TaxiManager getTaxiObserver(String taxiNumber);

    List<StoredBooking> getAllBookingsForTaxi(String taxiNumber);


}
