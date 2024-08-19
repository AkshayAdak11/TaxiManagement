package com.taxifleet.observer;


import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.services.CentralizedBookingService;
import com.taxifleet.strategy.BookingAssignmentStrategy;
import lombok.Data;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public class TaxiObserver {
    private final StoredTaxi taxi;
    private final BookingAssignmentStrategy assignmentStrategy;
    private final CentralizedBookingService centralizedBookingService;
    private final ConcurrentMap<StoredBooking, Boolean> availableBookings = new ConcurrentHashMap<>();

    @Inject
    public TaxiObserver(StoredTaxi taxi, BookingAssignmentStrategy assignmentStrategy,
                        CentralizedBookingService centralizedBookingService) {
        this.taxi = taxi;
        this.assignmentStrategy = assignmentStrategy;
        this.centralizedBookingService = centralizedBookingService;
    }

    public void update(StoredBooking storedBooking) {
        //Push in map according to pattern strategy
        if (this.assignmentStrategy.isEligibleToServeBooking(taxi, storedBooking)) {
            availableBookings.put(storedBooking, true);
        }
    }

    public boolean selectBooking(StoredBooking storedBooking) {
        if (Boolean.TRUE.equals(availableBookings.get(storedBooking))) {
            // Attempt booking to this taxi using the centralized service
            boolean assigned = centralizedBookingService.assignBookingToTaxi(taxi, storedBooking);
            if (assigned) {
                if (assignBooking(storedBooking)) {
                    //Remove booking from all observers map
                    centralizedBookingService.notifyObserversBookingCompleted(storedBooking);
                    centralizedBookingService.removeBookingFromAssignment(storedBooking);
                    return true;
                }
                centralizedBookingService.removeBookingFromAssignment(storedBooking);
            }
        }
        return false;
    }

    private boolean assignBooking(StoredBooking storedBooking) {
        if (taxi.isAvailable() && TaxiStatus.AVAILABLE.equals(taxi.getStatus())) {
            return assignmentStrategy.assignBooking(taxi, storedBooking);
        }
        return false;
    }


    public void removeBooking(StoredBooking storedBooking) {
        availableBookings.remove(storedBooking);
    }
}
