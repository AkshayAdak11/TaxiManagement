package com.taxifleet.factory;

import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.CentralizedBookingService;
import com.taxifleet.strategy.BookingAssignmentStrategy;
import com.taxifleet.observer.TaxiObserver;
import com.taxifleet.db.StoredTaxi;

import javax.inject.Inject;

public class TaxiObserverFactory {
    private final CentralizedBookingService centralizedBookingService;

    @Inject
    public TaxiObserverFactory(CentralizedBookingService centralizedBookingService) {
        this.centralizedBookingService = centralizedBookingService;
    }

    public TaxiObserver createObserver(StoredTaxi taxi, BookingAssignmentStrategy strategy) {
        return new TaxiObserver(taxi, strategy, centralizedBookingService);
    }
}
