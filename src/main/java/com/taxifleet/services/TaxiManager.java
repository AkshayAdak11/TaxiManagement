package com.taxifleet.services;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.services.BookingAssignmentService;
import com.taxifleet.strategy.BookingAssignmentStrategy;
import lombok.Data;
import lombok.Setter;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public class TaxiManager {
    private final StoredTaxi taxi;
    private final BookingAssignmentStrategy assignmentStrategy;
    private final BookingAssignmentService bookingAssignmentService;
    private final ConcurrentMap<Long, StoredBooking> availableBookings = new ConcurrentHashMap<>();

    @Inject
    public TaxiManager(StoredTaxi taxi, BookingAssignmentStrategy assignmentStrategy,
                       BookingAssignmentService bookingAssignmentService) {
        this.taxi = taxi;
        this.assignmentStrategy = assignmentStrategy;
        this.bookingAssignmentService = bookingAssignmentService;
    }

    public void update(StoredBooking storedBooking) {
        //Push in map according to pattern strategy
        if ((availableBookings.isEmpty() || !availableBookings.containsKey(storedBooking.getBookingId())) &&
                (this.assignmentStrategy.isEligibleToServeBooking(taxi, storedBooking))) {
                availableBookings.put(storedBooking.getBookingId(), storedBooking);
        }
    }


    public boolean selectBookingAndBookTaxi(StoredBooking storedBooking) {
        if (Objects.nonNull(availableBookings.get(storedBooking.getBookingId())) &&
                BookingStatus.PENDING.equals(storedBooking.getStatus())) {
            // Attempt booking to this taxi using the centralized service
            boolean isBookingAssignedToTaxi = bookingAssignmentService.assignBookingToTaxi(taxi, storedBooking);
            if (isBookingAssignedToTaxi) {
                if (bookTaxiWithPreference(storedBooking)) {
                    //Remove booking from all observers map to remove unused data and booking from all other taxis.
                    availableBookings.put(storedBooking.getBookingId(), storedBooking); // Updated stored Booking
                    bookingAssignmentService.notifyObserversBookingCompleted(storedBooking);
                    bookingAssignmentService.removeBookingFromAssignment(storedBooking);
                    return true;
                }
                bookingAssignmentService.removeBookingFromAssignment(storedBooking);
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
        availableBookings.remove(storedBooking.getBookingId());
    }
}
