package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.services.TaxiService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.MessagingService;
import com.taxifleet.services.BookingService;
import com.taxifleet.strategy.AllDistanceBasedAssignmentStrategy;
import com.taxifleet.strategy.BookingAssignmentStrategy;
import com.taxifleet.strategy.DistanceBasedAssignmentStrategy;

import javax.inject.Inject;

public class MessagingServiceImpl implements MessagingService {

    private final TaxiService taxiService;

    @Inject
    public MessagingServiceImpl(TaxiService taxiService) {
        this.taxiService = taxiService;
    }

    @Override
    public void notifyTaxis(StoredBooking storedBooking) {
        taxiService.notifyObservers(storedBooking);  // Notify taxis
    }
}
