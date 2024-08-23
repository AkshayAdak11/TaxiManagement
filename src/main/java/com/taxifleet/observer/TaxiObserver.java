package com.taxifleet.observer;


import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.services.CentralizedBookingService;
import com.taxifleet.strategy.BookingAssignmentStrategy;
import lombok.Data;
import lombok.Setter;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public class TaxiObserver {
    @Setter
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
        if ((availableBookings.isEmpty() || !availableBookings.containsKey(storedBooking)) &&
                (this.assignmentStrategy.isEligibleToServeBooking(taxi, storedBooking))) {
                availableBookings.put(storedBooking, true);
        }
    }


    public boolean selectBookingAndBookTaxi(StoredBooking storedBooking) {
        if (Boolean.TRUE.equals(availableBookings.get(storedBooking)) &&
                BookingStatus.PENDING.equals(storedBooking.getStatus())) {
            // Attempt booking to this taxi using the centralized service
            boolean isBookingAssignedToTaxi = centralizedBookingService.assignBookingToTaxi(taxi, storedBooking);
            if (isBookingAssignedToTaxi) {
                if (bookTaxiWithPreference(storedBooking)) {
                    //Remove booking from all observers map to remove unused data and booking from all other taxis.
                    centralizedBookingService.notifyObserversBookingCompleted(storedBooking);
                    centralizedBookingService.removeBookingFromAssignment(storedBooking);
                    return true;
                }
                centralizedBookingService.removeBookingFromAssignment(storedBooking);
            }
        }
        return false;
    }

    private boolean bookTaxiWithPreference(StoredBooking storedBooking) {
        if (taxi.isAvailable() && TaxiStatus.AVAILABLE.equals(taxi.getStatus())) {
            return assignmentStrategy.assignBooking(taxi, storedBooking);
        }
        return false;
    }


    public void removeBooking(StoredBooking storedBooking) {
        availableBookings.remove(storedBooking);
    }
}
