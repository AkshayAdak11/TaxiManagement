package com.taxifleet.repository;

import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.List;

public interface TaxiRepository {
    List<StoredTaxi> getAllTaxis();
    StoredTaxi getTaxi(Long id);
    StoredTaxi createTaxi(StoredTaxi taxi);
    StoredTaxi updateTaxi(StoredTaxi taxi);
    void deleteTaxi(Long id);
    void setTaxiAvailability(Long id, boolean available);
    List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius);
    StoredTaxi findNearByAvailableTaxi(Double latitude, Double longitude, Double radius);
}