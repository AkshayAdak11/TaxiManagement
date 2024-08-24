package com.taxifleet.services.impl;

import static org.junit.jupiter.api.Assertions.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class CachedTaxiServiceImplTest {

    private TaxiRepository taxiRepository;
    private BookingService bookingService;
    private TaxiObserverFactory taxiObserverFactory;
    private BookingAssignmentService bookingAssignmentService;
    private DashboardService dashboardService;
    private TaxisObserver taxiObservers;
    private Cache<String, StoredTaxi> taxiCache;
    private CachedTaxiServiceImpl cachedTaxiService;

    @BeforeEach
    void setUp() {
        taxiRepository = mock(TaxiRepository.class);
        bookingService = mock(BookingService.class);
        taxiObserverFactory = mock(TaxiObserverFactory.class);
        bookingAssignmentService = mock(BookingAssignmentService.class);
        dashboardService = mock(DashboardService.class);
        taxiObservers = mock(TaxisObserver.class);
        taxiCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
        cachedTaxiService = new CachedTaxiServiceImpl(taxiRepository, bookingService, taxiObserverFactory, bookingAssignmentService, dashboardService, taxiObservers);
    }

    @Test
    void testGetAllTaxis() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setTaxiNumber("TAXI1");
        List<StoredTaxi> taxis = List.of(taxi);
        when(taxiRepository.getAllTaxis()).thenReturn(taxis);

        List<StoredTaxi> result = cachedTaxiService.getAllTaxis();

        assertEquals(taxis, result);
        verify(taxiRepository).getAllTaxis();
    }

    @Test
    void testGetTaxi() {
        StoredTaxi taxi = new StoredTaxi();
        when(taxiRepository.getTaxi("123")).thenReturn(taxi);

        StoredTaxi result = cachedTaxiService.getTaxi("123");

        assertEquals(taxi, result);
        verify(taxiRepository).getTaxi("123");
    }

    @Test
    void testCreateTaxi() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setTaxiNumber("TAXI1");
        when(taxiRepository.createTaxi(taxi)).thenReturn(taxi);

        StoredTaxi result = cachedTaxiService.createTaxi(taxi);

        assertEquals(taxi, result);
        verify(taxiRepository).createTaxi(taxi);
    }

    @Test
    void testBookTaxi() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setTaxiNumber("TAXI1");
        when(taxiRepository.createTaxi(taxi)).thenReturn(taxi);

        StoredBooking booking = new StoredBooking();
        booking.setBookingId(1L);

        boolean result = cachedTaxiService.bookTaxi(taxi, booking);

        assertTrue(result);
        assertEquals(TaxiStatus.BOOKED, taxi.getStatus());
    }

    @Test
    void testUpdateTaxi() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setTaxiNumber("TAXI1");
        when(taxiRepository.createTaxi(taxi)).thenReturn(taxi);
        cachedTaxiService.createTaxi(taxi);

        cachedTaxiService.updateTaxi(taxi);


        verify(taxiRepository).updateTaxi(taxi);
    }

    @Test
    void testDeleteTaxi() {
        cachedTaxiService.deleteTaxi("123");

        verify(taxiRepository).deleteTaxi("123");
    }

    @Test
    void testUpdateTaxiAvailability() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setTaxiNumber("TAXI1");
        when(taxiRepository.createTaxi(taxi)).thenReturn(taxi);
        cachedTaxiService.createTaxi(taxi);
        TaxiManager observer = mock(TaxiManager.class);
        when(taxiObservers.getTaxiObserver("TAXI1")).thenReturn(observer);

        when(taxiRepository.getTaxi("TAXI1")).thenReturn(taxi);

        cachedTaxiService.updateTaxiAvailability("TAXI1", true, TaxiStatus.AVAILABLE);

        verify(taxiRepository).updateTaxi(taxi);
    }

    @Test
    void testFindNearbyTaxis() {
        List<StoredTaxi> taxis = List.of(new StoredTaxi());
        when(taxiRepository.findNearbyTaxis(12.9716, 77.5946, 10.0)).thenReturn(taxis);

        List<StoredTaxi> result = cachedTaxiService.findNearbyTaxis(12.9716, 77.5946, 10.0);

        assertEquals(taxis, result);
        verify(taxiRepository).findNearbyTaxis(12.9716, 77.5946, 10.0);
    }

    @Test
    void testUnsubscribeTaxi() {
        when(taxiObservers.removeObserver("123")).thenReturn(true);

        boolean result = cachedTaxiService.unsubscribeTaxi("123");

        assertTrue(result);
        verify(taxiObservers).removeObserver("123");
    }

    @Test
    void testSelectBooking() {
        StoredBooking booking = new StoredBooking();
        when(bookingService.getBooking(1L)).thenReturn(booking);
        TaxiManager observer = mock(TaxiManager.class);
        when(taxiObservers.getTaxiObserver("123")).thenReturn(observer);
        when(observer.selectBookingAndBookTaxi(booking)).thenReturn(true);

        Response response = cachedTaxiService.selectBooking("123", 1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllSubscribedTaxis() {
        List<TaxiManager> taxiManagers = List.of(mock(TaxiManager.class));
        when(taxiObservers.getAllTaxiManager()).thenReturn(taxiManagers);

        Response response = cachedTaxiService.getAllSubscribedTaxis();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}