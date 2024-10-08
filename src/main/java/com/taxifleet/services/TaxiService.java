package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;

import javax.ws.rs.core.Response;
import java.util.List;

public interface TaxiService {
    List<StoredTaxi> getAllTaxis();
    StoredTaxi getTaxi(String taxiNumber);
    StoredTaxi createTaxi(StoredTaxi taxi);
    boolean bookTaxi(StoredTaxi taxi, StoredBooking storedBooking);
    StoredTaxi updateTaxi(StoredTaxi taxi);
    void deleteTaxi(String taxiNumber);
    void updateTaxiAvailability(String taxiNumber, boolean available, TaxiStatus taxiStatus);
    List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius);

    boolean unsubscribeTaxi(String taxiNumber);

    Response selectBooking(String taxiNumber, Long bookingId);

    Response getAllSubscribedTaxis();
}