package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.patterns.TaxiObserver;

import java.util.List;

public interface TaxiService extends TaxiObserver {
    List<StoredTaxi> getAllTaxis();
    StoredTaxi getTaxi(Long id);
    StoredTaxi createTaxi(StoredTaxi taxi);
    StoredTaxi updateTaxi(StoredTaxi taxi);
    void deleteTaxi(Long id);
    void setTaxiAvailability(Long id, boolean available);
    List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius);
    StoredTaxi findNearByAvailableTaxi(Double latitude, Double longitude, Double radius);
    void updateTaxiStatus(StoredTaxi taxi, TaxiStatus status);
}