package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.observer.TaxisObserver;
import com.taxifleet.services.BookingAssignmentService;
import com.taxifleet.services.BookingService;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class BookingAssignmentServiceImpl implements BookingAssignmentService {

    private final TaxisObserver taxisObserver;

    @Getter
    private final ConcurrentMap<Long, StoredTaxi> bookingAssignments = new ConcurrentHashMap<>();

    @Inject
    public BookingAssignmentServiceImpl(TaxisObserver taxisObserver) {
        this.taxisObserver = taxisObserver;
    }

    @Override
    public boolean assignBookingToTaxi(StoredTaxi taxi, StoredBooking storedBooking) {
        long bookingId = storedBooking.getBookingId();
        boolean assigned = false;
        if (Objects.isNull(bookingAssignments.get(bookingId))) {
            assigned = bookingAssignments.putIfAbsent(bookingId, taxi) == null;
        }
        if (!assigned) {

            System.err.println("Booking already present with another taxi "+ bookingAssignments.get(bookingId).getTaxiNumber());
            return false;
        }
        return true;
    }

    @Override
    public void notifyObserversBookingCompleted(StoredBooking storedBooking) {
        taxisObserver.getAllTaxiManager()
                .forEach(observer -> observer.removeBooking(storedBooking));
    }

    @Override
    public void removeBookingFromAssignment(StoredBooking storedBooking) {
       bookingAssignments.remove(storedBooking.getBookingId());
    }
}
