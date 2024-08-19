package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.observer.TaxiObserver;

import java.util.List;

public interface CachedTaxiService {
    List<StoredTaxi> getAllTaxis();
    StoredTaxi getTaxi(Long id);
    StoredTaxi createTaxi(StoredTaxi taxi);
    boolean bookTaxi(StoredTaxi taxi, long bookingId);
    StoredTaxi updateTaxi(StoredTaxi taxi);
    void deleteTaxi(Long id);
    void setTaxiAvailability(Long id, boolean available);
    List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius);
    StoredTaxi findNearByAvailableTaxi(Double latitude, Double longitude, Double radius);
    void updateTaxiStatus(StoredTaxi taxi, TaxiStatus status);
    boolean isTaxiAvailable(); // New method
    StoredTaxi getTaxiAvailable();

    boolean subscribeTaxiToBookings(Long taxiId, BookingStrategy strategy);

    boolean unsubscribeTaxiToBookings(Long taxiId);

    void notifyTaxis(StoredBooking storedBooking);

    void notifyTaxiAboutBooking(StoredTaxi taxi, StoredBooking storedBooking);

    boolean selectBooking(long taxiId, long bookingId);


    StoredBooking getBookingTaxis(long bookingId);
    TaxiObserver getTaxiObserver(long taxiId);

    List<TaxiObserver> getAllTaxiObserver();
}