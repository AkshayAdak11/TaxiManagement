package com.taxifleet.observer;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.services.TaxiManager;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseObserver implements Observer {
    protected final List<TaxiManager> taxiManagers;

    protected BaseObserver() {
        taxiManagers = new ArrayList<>();
    }

    @Override
    public void registerObserver(TaxiManager observer) {
        taxiManagers.add(observer);
    }

    @Override
    public List<TaxiManager> getAllTaxiObserver() {
        return taxiManagers;
    }

    public void notifyObservers(StoredBooking storedBooking) {
        for (TaxiManager observer : taxiManagers) {
            observer.update(storedBooking);
        }
    }

    @Override
    public boolean removeObserver(String taxiNumber) {
        return taxiManagers.removeIf(observer -> observer.getTaxi().getTaxiNumber().equals(taxiNumber));
    }
}
