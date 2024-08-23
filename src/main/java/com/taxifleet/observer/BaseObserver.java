package com.taxifleet.observer;

import com.taxifleet.db.StoredBooking;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseObserver implements Observer {
    protected final List<TaxiObserver> taxiObservers;

    protected BaseObserver() {
        taxiObservers = new ArrayList<>();
    }

    @Override
    public void registerObserver(TaxiObserver observer) {
        taxiObservers.add(observer);
    }

    @Override
    public List<TaxiObserver> getAllTaxiObserver() {
        return taxiObservers;
    }

    public void notifyObservers(StoredBooking storedBooking) {
        for (TaxiObserver observer : taxiObservers) {
            observer.update(storedBooking);
        }
    }

    @Override
    public boolean removeObserver(String taxiNumber) {
        return taxiObservers.removeIf(observer -> observer.getTaxi().getTaxiNumber().equals(taxiNumber));
    }
}
