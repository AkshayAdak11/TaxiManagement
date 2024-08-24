package com.taxifleet.repository;

import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.List;

public interface TaxiRepository {
    List<StoredTaxi> getAllTaxis();
    StoredTaxi getTaxi(String taxiNumber);
    StoredTaxi createTaxi(StoredTaxi taxi);
    void updateTaxi(StoredTaxi taxi);
    void deleteTaxi(String taxiNumber);
    List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius);
}