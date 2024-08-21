package com.taxifleet.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.observer.TaxiObserver;
import com.taxifleet.repository.TaxiRepository;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.MessagingService;
import com.taxifleet.services.TaxiObserverFactory;
import com.taxifleet.strategy.BookingAssignmentStrategy;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class CachedTaxiServiceImpl implements CachedTaxiService {
    private final Cache<String, StoredTaxi> taxiCache;
    private final TaxiRepository taxiRepository;
    private final MessagingService messagingService;
    private final com.taxifleet.factory.TaxiObserverFactory taxiObserverFactory;
    private final List<TaxiObserver> taxiObservers = new ArrayList<>();
    private final BookingService bookingService;

    @Inject
    public CachedTaxiServiceImpl(TaxiRepository taxiRepository,
                                 MessagingService messagingService,
                                 com.taxifleet.factory.TaxiObserverFactory taxiObserverFactory,
                                 BookingService bookingService) {
        this.taxiRepository = taxiRepository;
        this.messagingService = messagingService;
        this.taxiObserverFactory = taxiObserverFactory;
        this.bookingService = bookingService;
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
        return createdTaxi;
    }


    @Override
    public synchronized boolean bookTaxi(StoredTaxi taxi, long bookingID, double toLatitude, double toLongitude) {
        taxi.setAvailable(false);
        taxi.setBookingId(bookingID);
        taxi.setToLatitude(toLatitude);
        taxi.setToLongitude(toLongitude);
        taxi.setStatus(TaxiStatus.BOOKED);
        StoredTaxi storedTaxi = updateTaxi(taxi);
        return TaxiStatus.BOOKED.equals(storedTaxi.getStatus());
    }

    @Override
    public StoredTaxi updateTaxi(StoredTaxi taxi) {
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
    public boolean subscribeTaxiToBookings(String taxiNumber, BookingStrategy bookingStrategy) {
        // Check if a TaxiObserver for the given taxiNumber already exists
        boolean observerExists = taxiObservers.stream()
                .anyMatch(observer -> observer.getTaxi().getTaxiNumber().equals(taxiNumber));

        if (observerExists) {
            return false;
        }

        StoredTaxi taxi = getTaxi(taxiNumber);
        BookingAssignmentStrategy bookingAssignmentStrategy = messagingService.createStrategy(bookingStrategy);
        if (taxi != null) {
            TaxiObserver observer = taxiObserverFactory.createObserver(taxi, bookingAssignmentStrategy);
            taxiObservers.add(observer);
            return true;
        }
        return false;
    }

    @Override
    public boolean unsubscribeTaxiToBookings(String taxiNumber) {
        return taxiObservers.removeIf(observer -> observer.getTaxi().getTaxiNumber().equals(taxiNumber));
    }

    public void notifyTaxis(StoredBooking storedBooking) {
        for (TaxiObserver observer : taxiObservers) {
            observer.update(storedBooking);
        }
    }

    @Override
    public List<StoredBooking> getAllBookingsAsPerChoice(String taxiNumber) {
        for (TaxiObserver observer : taxiObservers) {
            if (observer.getTaxi().getTaxiNumber().equals(taxiNumber)) {
                return new ArrayList<>(observer.getAvailableBookings().keySet());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public StoredBooking getBookingTaxis(long bookingId) {
        return bookingService.getBooking(bookingId);
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
}