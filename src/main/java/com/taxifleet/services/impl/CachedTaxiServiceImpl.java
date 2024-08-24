package com.taxifleet.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.factory.TaxiObserverFactory;
import com.taxifleet.observer.TaxisObserver;
import com.taxifleet.repository.TaxiRepository;
import com.taxifleet.services.BookingAssignmentService;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.TaxiManager;
import com.taxifleet.services.TaxiService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class CachedTaxiServiceImpl implements TaxiService {
    private final Cache<String, StoredTaxi> taxiCache;
    private final TaxiRepository taxiRepository;
    private final BookingService bookingService;
    private final TaxiObserverFactory taxiObserverFactory;

    private final BookingAssignmentService bookingAssignmentService;
    private final DashboardService dashboardService;

    private final TaxisObserver taxiObservers;

    @Inject
    public CachedTaxiServiceImpl(TaxiRepository taxiRepository,
                                 BookingService bookingService,
                                 TaxiObserverFactory taxiObserverFactory,
                                 BookingAssignmentService bookingAssignmentService,
                                 DashboardService dashboardService,
                                 TaxisObserver taxiObservers) {
        this.taxiRepository = taxiRepository;
        this.bookingService = bookingService;
        this.taxiObserverFactory = taxiObserverFactory;
        this.bookingAssignmentService = bookingAssignmentService;
        this.dashboardService = dashboardService;
        this.taxiObservers = taxiObservers;
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
        taxiObservers.registerObserver(taxiObserverFactory.createObserver(taxi,
                taxiObserverFactory.createStrategy(taxi.getBookingStrategy(),
                        this, bookingService, dashboardService), bookingAssignmentService));
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
        taxi.setCurrentLatitude(storedBooking.getToLatitude());
        taxi.setCurrentLongitude(storedBooking.getToLongitude());
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
            TaxiManager observer = taxiObservers.getTaxiObserver(taxiNumber);
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
        return taxiObservers.removeObserver(taxiNumber);
    }

    @Override
    public Response selectBooking(String taxiNumber, Long bookingId) {
        StoredBooking storedBooking = bookingService.getBooking(bookingId);
        TaxiManager observer = taxiObservers.getTaxiObserver(taxiNumber);

        if (observer != null && storedBooking != null) {
            boolean success = observer.selectBookingAndBookTaxi(storedBooking);
            if (success) {
                return Response.ok().entity("Booking selected successfully").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Failed to select booking").build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Taxi or booking not found").build();
        }
    }

    @Override
    public Response getAllSubscribedTaxis() {
        List<TaxiManager> taxisObservers = taxiObservers.getAllTaxiManager();

        return Response.ok().entity(taxisObservers.stream().map(TaxiManager::getTaxi).toList()).build();
    }
}