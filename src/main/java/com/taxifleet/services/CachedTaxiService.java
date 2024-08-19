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
    boolean bookTaxi(StoredTaxi taxi, long bookingId);
    StoredTaxi updateTaxi(StoredTaxi taxi);
    void deleteTaxi(String taxiNumber);
    void setTaxiAvailability(String taxiNumber, boolean available);
    List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius);
    StoredTaxi findNearByAvailableTaxi(Double latitude, Double longitude, Double radius);
    void updateTaxiStatus(StoredTaxi taxi, TaxiStatus status);
    boolean isTaxiAvailable(); // New method
    StoredTaxi getTaxiAvailable();

    boolean subscribeTaxiToBookings(String taxiNumber, BookingStrategy strategy);

    boolean unsubscribeTaxiToBookings(String taxiNumber);

    void notifyTaxis(StoredBooking storedBooking);

    boolean selectBooking(String taxiId, long bookingId);

    List<StoredBooking> getAllBookingsAsPerChoice(String taxiId);

    StoredBooking getBookingTaxis(long bookingId);
    TaxiObserver getTaxiObserver(String taxiNumber);

    List<TaxiObserver> getAllTaxiObserver();
}