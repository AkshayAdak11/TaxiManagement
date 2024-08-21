package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.observer.TaxiObserver;

import java.util.List;

public interface CachedTaxiService {
    List<StoredTaxi> getAllTaxis();
    StoredTaxi getTaxi(String taxiNumber);
    StoredTaxi createTaxi(StoredTaxi taxi);
    boolean bookTaxi(StoredTaxi taxi, long bookingId, double toLatitude, double toLongitude);
    StoredTaxi updateTaxi(StoredTaxi taxi);
    void deleteTaxi(String taxiNumber);
    void updateTaxiAvailability(String taxiNumber, boolean available, TaxiStatus taxiStatus);
    List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius);

    boolean subscribeTaxiToBookings(String taxiNumber, BookingStrategy strategy);

    boolean unsubscribeTaxiToBookings(String taxiNumber);

    void notifyTaxis(StoredBooking storedBooking);
    List<StoredBooking> getAllBookingsAsPerChoice(String taxiId);

    StoredBooking getBookingTaxis(long bookingId);
    TaxiObserver getTaxiObserver(String taxiNumber);

    List<TaxiObserver> getAllTaxiObserver();
}