package com.taxifleet.strategy;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.model.Location;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.TaxiService;

import javax.inject.Inject;


public class DistanceBasedAssignmentStrategy implements BookingAssignmentStrategy {
    private final double maxDistance;
    private final TaxiService taxiService;
    private final BookingService bookingService;

    private final DashboardService dashboardService;

    @Inject
    public DistanceBasedAssignmentStrategy(double maxDistance,
                                           TaxiService taxiService,
                                           BookingService bookingService,
                                           DashboardService dashboardService) {
        this.maxDistance = maxDistance;
        this.taxiService = taxiService;
        this.bookingService = bookingService;
        this.dashboardService = dashboardService;
    }

    @Override
    public boolean assignBooking(StoredTaxi taxi, StoredBooking storedBooking) {
        if (isNearBy(storedBooking, taxi) && (taxiService.bookTaxi(taxi, storedBooking))) {
            bookingService.confirmBooking(storedBooking, taxi.getTaxiNumber());
            dashboardService.updateStats(storedBooking, taxi.getTaxiNumber(), BookingStatus.COMPLETED);
            return true;

        }
        return false;
    }

    @Override
    public boolean isEligibleToServeBooking(StoredTaxi taxi, StoredBooking storedBooking) {
        return isNearBy(storedBooking, taxi);
    }

    public boolean isNearBy(StoredBooking storedBooking, StoredTaxi taxi) {
        Location bookingLocation = new Location(storedBooking.getFromLatitude(),
                storedBooking.getFromLongitude());
        Location taxiLocation = new Location(taxi.getCurrentLatitude(), taxi.getCurrentLongitude());

        double distance = taxiLocation.distanceTo(bookingLocation.getLatitude(), bookingLocation.getLongitude());
        return distance <= maxDistance && taxi.isAvailable() && TaxiStatus.AVAILABLE.equals(taxi.getStatus()) &&
                BookingStatus.PENDING.equals(storedBooking.getStatus());
    }
}

