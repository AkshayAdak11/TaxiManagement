package com.taxifleet.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.factory.TaxiObserverFactory;
import com.taxifleet.observer.TaxiObserver;
import com.taxifleet.repository.TaxiRepository;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.CentralizedBookingService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.TaxiService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class CachedTaxiServiceImpl implements TaxiService {
    private final Cache<String, StoredTaxi> taxiCache;
    private final TaxiRepository taxiRepository;
    private final List<TaxiObserver> taxiObservers = new ArrayList<>();
    private final BookingService bookingService;
    private final TaxiObserverFactory taxiObserverFactory;

    private final CentralizedBookingService centralizedBookingService;
    private final DashboardService dashboardService;

    @Inject
    public CachedTaxiServiceImpl(TaxiRepository taxiRepository,
                                 BookingService bookingService,
                                 TaxiObserverFactory taxiObserverFactory,
                                 CentralizedBookingService centralizedBookingService,
                                 DashboardService dashboardService) {
        this.taxiRepository = taxiRepository;
        this.bookingService = bookingService;
        this.taxiObserverFactory = taxiObserverFactory;
        this.centralizedBookingService = centralizedBookingService;
        this.dashboardService = dashboardService;
        this.taxiCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }

    @Override
    public List<StoredTaxi> getAllTaxis() {
        List<StoredTaxi> taxis = taxiRepository.getAllTaxis();
        taxis.forEach(taxi -> taxiCache.put(taxi.getTaxiNumber(), taxi));
        return taxis;
    }

    @Override
    public StoredTaxi getTaxi(String taxiNumber) {
        StoredTaxi taxi = taxiCache.getIfPresent(taxiNumber);
        if (taxi == null) {
            taxi = taxiRepository.getTaxi(taxiNumber);
            if (taxi != null) {
                taxiCache.put(taxiNumber, taxi);
            }
        }
        return taxi;
    }

    @Override
    public StoredTaxi createTaxi(StoredTaxi taxi) {
        StoredTaxi createdTaxi = taxiRepository.createTaxi(taxi);
        taxiCache.put(createdTaxi.getTaxiNumber(), createdTaxi);
        registerObserver(taxiObserverFactory.createObserver(taxi,
                taxiObserverFactory.createStrategy(taxi.getBookingStrategy(),
                        this, bookingService, dashboardService), centralizedBookingService));
        return createdTaxi;
    }


    @Override
    public boolean bookTaxi(StoredTaxi taxi, StoredBooking storedBooking) {
        taxi.setAvailable(false);
        taxi.setBookingId(storedBooking.getBookingId());
        taxi.setToLatitude(storedBooking.getToLatitude());
        taxi.setToLongitude(storedBooking.getToLongitude());
        taxi.setFromLatitude(storedBooking.getFromLatitude());
        taxi.setFromLongitude(storedBooking.getFromLongitude());
        taxi.setStatus(TaxiStatus.BOOKED);
        StoredTaxi storedTaxi = updateTaxi(taxi);
        return TaxiStatus.BOOKED.equals(storedTaxi.getStatus());
    }

    @Override
    public StoredTaxi updateTaxi(StoredTaxi taxi) {
        taxiCache.invalidate(taxi);
        taxiRepository.updateTaxi(taxi);
        taxiCache.put(taxi.getTaxiNumber(), taxi);
        return taxi;
    }

    @Override
    public void deleteTaxi(String taxiNumber) {
        taxiRepository.deleteTaxi(taxiNumber);
        taxiCache.invalidate(taxiNumber);
    }

    @Override
    public void updateTaxiAvailability(String taxiNumber, boolean available, TaxiStatus taxiStatus) {
        StoredTaxi taxi = taxiRepository.getTaxi(taxiNumber);
        if (taxi != null) {
            taxi.setAvailable(available);
            taxi.setStatus(taxiStatus);
            taxiRepository.updateTaxi(taxi);
            StoredTaxi updatedTaxi = taxiRepository.getTaxi(taxiNumber);
            TaxiObserver observer = getTaxiObserver(taxiNumber);
            TaxiObserverFactory.updateTaxiObserver(observer, updatedTaxi);
            taxiCache.put(updatedTaxi.getTaxiNumber(), updatedTaxi);
        }
    }

    @Override
    public List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius) {
        return taxiRepository.findNearbyTaxis(latitude, longitude, radius);
    }

    @Override
    public boolean unsubscribeTaxi(String taxiNumber) {
        return removeObserver(taxiNumber);
    }

    @Override
    public List<StoredBooking> getAllBookingsForTaxiByPreference(String taxiNumber) {
        for (TaxiObserver observer : taxiObservers) {
            if (observer.getTaxi().getTaxiNumber().equals(taxiNumber)) {
                return new ArrayList<>(observer.getAvailableBookings().values());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public TaxiObserver getTaxiObserver(String taxiNumber) {
        for (TaxiObserver observer : taxiObservers) {
            if (observer.getTaxi().getTaxiNumber().equals(taxiNumber)) {
                return observer;
            }
        }
        return null;
    }

    @Override
    public List<TaxiObserver> getAllTaxiObserver() {
        return taxiObservers;
    }

    private void registerObserver(TaxiObserver observer) {
        taxiObservers.add(observer);
    }

    public void notifyObservers(StoredBooking storedBooking) {
        for (TaxiObserver observer : taxiObservers) {
            observer.update(storedBooking);
        }
    }

    private boolean removeObserver(String taxiNumber) {
        return taxiObservers.removeIf(observer -> observer.getTaxi().getTaxiNumber().equals(taxiNumber));
    }
}