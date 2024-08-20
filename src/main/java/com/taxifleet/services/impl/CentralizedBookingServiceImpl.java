package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.CentralizedBookingService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class CentralizedBookingServiceImpl implements CentralizedBookingService {

    private final CachedTaxiService cachedTaxiService;

    private final BookingService bookingService;
    private final ConcurrentMap<Long, StoredTaxi> bookingAssignments = new ConcurrentHashMap<>();

    @Inject
    public CentralizedBookingServiceImpl(CachedTaxiService cachedTaxiService,
                                         BookingService bookingService) {
        this.cachedTaxiService = cachedTaxiService;
        this.bookingService = bookingService;
    }

    @Override
    public boolean assignBookingToTaxi(StoredTaxi taxi, StoredBooking storedBooking) {
        long bookingId = storedBooking.getBookingId();
        if (Objects.isNull(bookingAssignments.get(bookingId))) {
            return bookingAssignments.putIfAbsent(bookingId, taxi) == null;
        }
        System.err.println("Booking already present with another taxi");
        return false;
    }

    @Override
    public void notifyObserversBookingCompleted(StoredBooking storedBooking) {
        cachedTaxiService.getAllTaxiObserver()
                .forEach(observer -> observer.removeBooking(storedBooking));
    }

    @Override
    public void removeBookingFromAssignment(StoredBooking storedBooking) {
       bookingAssignments.remove(storedBooking.getBookingId());
    }
    @Override
    public void notifyOtherObserversIfBookingIsCompleted(StoredBooking storedBooking) {
        BookingStatus bookingStatus = bookingService.getBooking(storedBooking.getBookingId()).getStatus();
        if (!BookingStatus.PENDING.equals(bookingStatus)) {
            notifyObserversBookingCompleted(storedBooking);
        }
    }
}
