package com.taxifleet.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.model.Location;
import com.taxifleet.repository.TaxiRepository;
import com.taxifleet.services.CachedTaxiService;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CachedTaxiServiceImpl implements CachedTaxiService {
    private final Cache<Long, StoredTaxi> taxiCache;
    private final TaxiRepository taxiRepository;

    @Inject
    public CachedTaxiServiceImpl(TaxiRepository taxiRepository) {
        this.taxiRepository = taxiRepository;
        this.taxiCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }

    @Override
    public List<StoredTaxi> getAllTaxis() {
        List<StoredTaxi> taxis = taxiRepository.getAllTaxis();
        taxis.forEach(taxi -> taxiCache.put(taxi.getId(), taxi));
        return taxis;
    }

    @Override
    public StoredTaxi getTaxi(Long id) {
        StoredTaxi taxi = taxiCache.getIfPresent(id);
        if (taxi == null) {
            taxi = taxiRepository.getTaxi(id);
            if (taxi != null) {
                taxiCache.put(id, taxi);
            }
        }
        return taxi;
    }

    @Override
    public StoredTaxi createTaxi(StoredTaxi taxi) {
        StoredTaxi createdTaxi = taxiRepository.createTaxi(taxi);
        taxiCache.put(createdTaxi.getId(), createdTaxi);
        return createdTaxi;
    }


    @Override
    public boolean bookTaxi(StoredTaxi taxi, long bookingID) {
        taxi.setAvailable(false);
        taxi.setBookingId(bookingID);
        taxi.setStatus(TaxiStatus.BOOKED);
        StoredTaxi storedTaxi = updateTaxi(taxi);
        return TaxiStatus.BOOKED.equals(storedTaxi.getStatus());
    }

    @Override
    public StoredTaxi updateTaxi(StoredTaxi taxi) {
        StoredTaxi updatedTaxi = taxiRepository.updateTaxi(taxi);
        taxiCache.put(updatedTaxi.getId(), updatedTaxi);
        return updatedTaxi;
    }

    @Override
    public void deleteTaxi(Long id) {
        taxiRepository.deleteTaxi(id);
        taxiCache.invalidate(id);
    }

    @Override
    public void setTaxiAvailability(Long id, boolean available) {
        StoredTaxi taxi = taxiRepository.getTaxi(id);
        if (taxi != null) {
            taxi.setAvailable(available);
            taxiRepository.updateTaxi(taxi);
            taxiCache.invalidate(id);
        }
    }

    @Override
    public List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius) {
        return taxiRepository.findNearbyTaxis(latitude, longitude, radius);
    }

    @Override
    public StoredTaxi findNearByAvailableTaxi(Double latitude, Double longitude, Double radius) {
        return taxiRepository.findNearbyTaxis(latitude, longitude, radius)
                .stream()
                .filter(StoredTaxi::isAvailable)
                .sorted(Comparator.comparingDouble(taxi -> new Location(taxi.getLatitude(), taxi.getLongitude())
                        .distanceTo(latitude, longitude)))
                .filter(storedTaxi -> TaxiStatus.AVAILABLE.equals(storedTaxi.getStatus()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updateTaxiStatus(StoredTaxi taxi, TaxiStatus status) {
        taxi.setStatus(status);
        taxiRepository.updateTaxi(taxi);
        System.out.println("Updated taxi status to: " + status);
    }

    @Override
    public boolean isTaxiAvailable() {
        return taxiRepository.getAllTaxis().stream()
                .anyMatch(taxi -> taxi.isAvailable() && TaxiStatus.AVAILABLE.equals(taxi.getStatus()));
    }

    public StoredTaxi getTaxiAvailable() {
        return taxiRepository.getAllTaxis().stream()
                .filter(taxi -> taxi.isAvailable() && TaxiStatus.AVAILABLE.equals(taxi.getStatus()))
                .findFirst()
                .orElse(null);
    }
}