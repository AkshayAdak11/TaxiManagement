package com.taxifleet.strategy;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.CachedTaxiService;

import javax.inject.Inject;

public class AllDistanceBasedAssignmentStrategy implements BookingAssignmentStrategy {
    private final CachedTaxiService cachedTaxiService;
    private final BookingService bookingService;

    @Inject
    public AllDistanceBasedAssignmentStrategy(CachedTaxiService cachedTaxiService,
                                              BookingService bookingService) {
        this.cachedTaxiService = cachedTaxiService;
        this.bookingService = bookingService;
    }

    @Override
    public boolean assignBooking(StoredTaxi taxi, StoredBooking storedBooking) {
        if (taxi.isAvailable()) {
            taxi.setAvailable(false);
            taxi.setBookingId(storedBooking.getId());
            taxi.setStatus(TaxiStatus.BOOKED);
            boolean bookedTaxi = cachedTaxiService.bookTaxi(taxi, storedBooking.getBookingId());
            if (bookedTaxi) {
                bookingService.confirmBooking(storedBooking, taxi.getTaxiNumber());
            }
            return bookedTaxi;
        }
        return false;
    }

    @Override
    public boolean isEligibleToServeBooking(StoredTaxi taxi, StoredBooking storedBooking) {
        return true;
    }
}

