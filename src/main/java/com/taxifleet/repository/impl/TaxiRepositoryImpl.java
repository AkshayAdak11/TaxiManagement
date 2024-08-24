package com.taxifleet.repository.impl;

import com.taxifleet.db.StoredTaxi;
import com.taxifleet.db.dao.TaxiDAO;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.repository.TaxiRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class TaxiRepositoryImpl implements TaxiRepository {

    private final TaxiDAO taxiDAO;

    @Inject
    public TaxiRepositoryImpl(TaxiDAO taxiDAO) {
        this.taxiDAO = taxiDAO;
    }

    @Override
    public StoredTaxi createTaxi(StoredTaxi taxi) {
        return taxiDAO.create(taxi);
    }

    @Override
    public StoredTaxi getTaxi(String taxiNumber) {
        return taxiDAO.findByTaxiNumber(taxiNumber);
    }

    @Override
    public List<StoredTaxi> getAllTaxis() {
        return taxiDAO.findAll();
    }

    @Override
    public void updateTaxi(StoredTaxi taxi) {
        taxiDAO.updateTaxi(taxi);
    }

    @Override
    public void deleteTaxi(String taxiNumber) {
        StoredTaxi taxi = taxiDAO.findByTaxiNumber(taxiNumber);
        if (taxi != null) {
            taxiDAO.deleteTaxi(taxi);
        }
    }

    @Override
    public List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius) {
        return taxiDAO.findNearbyTaxis(latitude, longitude, radius);
    }
}