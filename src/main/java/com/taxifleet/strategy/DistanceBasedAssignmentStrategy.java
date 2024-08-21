package com.taxifleet.strategy;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.model.Location;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.DashboardService;

public class DistanceBasedAssignmentStrategy implements BookingAssignmentStrategy {
    private final double maxDistance;
    private final CachedTaxiService cachedTaxiService;
    private final BookingService bookingService;

    private final DashboardService dashboardService;

    public DistanceBasedAssignmentStrategy(double maxDistance,
                                           CachedTaxiService cachedTaxiService,
                                           BookingService bookingService,
                                           DashboardService dashboardService) {
        this.maxDistance = maxDistance;
        this.cachedTaxiService = cachedTaxiService;
        this.bookingService = bookingService;
        this.dashboardService = dashboardService;
    }

    @Override
    public boolean assignBooking(StoredTaxi taxi, StoredBooking storedBooking) {
        if (isNearBy(storedBooking, taxi) && (cachedTaxiService.bookTaxi(taxi, storedBooking.getBookingId(),
                storedBooking.getToLatitude(), storedBooking.getToLongitude()))) {
                bookingService.confirmBooking(storedBooking, taxi.getTaxiNumber());
                dashboardService.updateDashboardStats(storedBooking.getBookingId(), taxi.getTaxiNumber(), BookingStatus.COMPLETED);
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
        Location taxiLocation = new Location(taxi.getFromLatitude(), taxi.getFromLongitude());

        double distance = taxiLocation.distanceTo(bookingLocation.getLatitude(), bookingLocation.getLongitude());
        return distance <= maxDistance && taxi.isAvailable() && TaxiStatus.AVAILABLE.equals(taxi.getStatus()) &&
                BookingStatus.PENDING.equals(storedBooking.getStatus());
    }
}

