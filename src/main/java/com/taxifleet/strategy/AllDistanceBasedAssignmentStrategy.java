package com.taxifleet.strategy;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.TaxiService;
import com.taxifleet.services.DashboardService;

import javax.inject.Inject;

public class AllDistanceBasedAssignmentStrategy implements BookingAssignmentStrategy {
    private final TaxiService taxiService;
    private final BookingService bookingService;

    private final DashboardService dashboardService;

    @Inject
    public AllDistanceBasedAssignmentStrategy(TaxiService taxiService,
                                              BookingService bookingService,
                                              DashboardService dashboardService) {
        this.taxiService = taxiService;
        this.bookingService = bookingService;
        this.dashboardService = dashboardService;
    }

    @Override
    public boolean assignBooking(StoredTaxi taxi, StoredBooking storedBooking) {
        if (taxi.isAvailable()) {
            taxi.setAvailable(false);
            taxi.setBookingId(storedBooking.getBookingId());
            taxi.setStatus(TaxiStatus.BOOKED);
            boolean bookedTaxi = taxiService.bookTaxi(taxi, storedBooking);
            if (bookedTaxi) {
                bookingService.confirmBooking(storedBooking, taxi.getTaxiNumber());
            }
            dashboardService.updateStats(storedBooking, taxi.getTaxiNumber(), BookingStatus.COMPLETED);
            return bookedTaxi;
        }
        return false;
    }

    @Override
    public boolean isEligibleToServeBooking(StoredTaxi taxi, StoredBooking storedBooking) {
        return TaxiStatus.AVAILABLE.equals(taxi.getStatus()) && taxi.isAvailable() &&
                BookingStatus.PENDING.equals(storedBooking.getStatus());
    }
}

