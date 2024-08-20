package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.MessagingService;
import com.taxifleet.services.BookingService;
import com.taxifleet.strategy.AllDistanceBasedAssignmentStrategy;
import com.taxifleet.strategy.BookingAssignmentStrategy;
import com.taxifleet.strategy.DistanceBasedAssignmentStrategy;

import javax.inject.Inject;

public class MessagingServiceImpl implements MessagingService {

    private final CachedTaxiService cachedTaxiService;
    private final BookingService bookingService;

    private final DashboardService dashboardService;

    @Inject
    public MessagingServiceImpl(CachedTaxiService cachedTaxiService,
                                BookingService bookingService,
                                DashboardService dashboardService) {
        this.cachedTaxiService = cachedTaxiService;
        this.bookingService = bookingService;
        this.dashboardService = dashboardService;
    }

    @Override
    public void notifyTaxis(StoredBooking storedBooking) {
        cachedTaxiService.notifyTaxis(storedBooking);  // Notify taxis directly
    }

    @Override
    public BookingAssignmentStrategy createStrategy(BookingStrategy bookingStrategy) {
        return switch (bookingStrategy) {
            case NEAR_BY -> new DistanceBasedAssignmentStrategy(10, cachedTaxiService, bookingService, dashboardService);
            case ALL_AREA -> new AllDistanceBasedAssignmentStrategy(cachedTaxiService, bookingService, dashboardService);
            default -> throw new IllegalArgumentException("Unknown strategy type: " + bookingStrategy);
        };
    }
}
