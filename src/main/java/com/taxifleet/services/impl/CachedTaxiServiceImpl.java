package com.taxifleet.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.factory.TaxiObserverFactory;
import com.taxifleet.model.Location;
import com.taxifleet.observer.TaxiObserver;
import com.taxifleet.repository.TaxiRepository;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.MessagingService;
import com.taxifleet.strategy.BookingAssignmentStrategy;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class CachedTaxiServiceImpl implements CachedTaxiService {
    private final Cache<String, StoredTaxi> taxiCache;
    private final TaxiRepository taxiRepository;
    private final MessagingService messagingService;
    private final TaxiObserverFactory taxiObserverFactory;
    private final List<TaxiObserver> taxiObservers = new ArrayList<>();
    private final BookingService bookingService;

    @Inject
    public CachedTaxiServiceImpl(TaxiRepository taxiRepository,
                                 MessagingService messagingService,
                                 TaxiObserverFactory taxiObserverFactory,
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
    public synchronized boolean bookTaxi(StoredTaxi taxi, long bookingID) {
        taxi.setAvailable(false);
        taxi.setBookingId(bookingID);
        taxi.setStatus(TaxiStatus.BOOKED);
        StoredTaxi storedTaxi = updateTaxi(taxi);
        return TaxiStatus.BOOKED.equals(storedTaxi.getStatus());
    }

    @Override
    public StoredTaxi updateTaxi(StoredTaxi taxi) {
        StoredTaxi updatedTaxi = taxiRepository.updateTaxi(taxi);
        taxiCache.put(updatedTaxi.getTaxiNumber(), updatedTaxi);
        return updatedTaxi;
    }

    @Override
    public void deleteTaxi(String taxiNumber) {
        taxiRepository.deleteTaxi(taxiNumber);
        taxiCache.invalidate(taxiNumber);
    }

    @Override
    public void setTaxiAvailability(String taxiNumber, boolean available) {
        StoredTaxi taxi = taxiRepository.getTaxi(taxiNumber);
        if (taxi != null) {
            taxi.setAvailable(available);
            taxiRepository.updateTaxi(taxi);
            taxiCache.invalidate(taxiNumber);
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

    public synchronized StoredTaxi getTaxiAvailable() {
        //BOOKING in PROGRESS
        return taxiRepository.getAllTaxis().stream()
                .filter(taxi -> taxi.isAvailable() && TaxiStatus.AVAILABLE.equals(taxi.getStatus()))
                .findFirst()
                .orElse(null);
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
    public boolean selectBooking(String taxiId, long bookingId) {
        return false;
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