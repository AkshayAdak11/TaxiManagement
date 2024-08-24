package com.taxifleet.strategy;


import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.TaxiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DistanceBasedAssignmentStrategyTest {

    private TaxiService taxiService;
    private BookingService bookingService;
    private DashboardService dashboardService;
    private DistanceBasedAssignmentStrategy strategy;
    private final double maxDistance = 10.0;

    @BeforeEach
    void setUp() {
        taxiService = mock(TaxiService.class);
        bookingService = mock(BookingService.class);
        dashboardService = mock(DashboardService.class);
        strategy = new DistanceBasedAssignmentStrategy(maxDistance, taxiService, bookingService, dashboardService);
    }

    @Test
    void testAssignBooking_Success() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setAvailable(true);
        taxi.setStatus(TaxiStatus.AVAILABLE);
        taxi.setFromLatitude(12.9716);
        taxi.setFromLongitude(77.5946);

        StoredBooking booking = new StoredBooking();
        booking.setBookingId(1L);
        booking.setFromLatitude(12.9716);
        booking.setFromLongitude(77.5946);
        booking.setStatus(BookingStatus.PENDING);

        when(taxiService.bookTaxi(taxi, booking)).thenReturn(true);

        boolean result = strategy.assignBooking(taxi, booking);

        assertTrue(result);
        verify(bookingService).confirmBooking(booking, taxi.getTaxiNumber());
        verify(dashboardService).updateStats(booking, taxi.getTaxiNumber(), BookingStatus.COMPLETED);
    }

    @Test
    void testAssignBooking_Failure() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setAvailable(false);
        taxi.setStatus(TaxiStatus.BOOKED);
        taxi.setFromLatitude(12.9716);
        taxi.setFromLongitude(77.5946);

        StoredBooking booking = new StoredBooking();
        booking.setBookingId(1L);
        booking.setFromLatitude(12.9716);
        booking.setFromLongitude(77.5946);

        boolean result = strategy.assignBooking(taxi, booking);

        assertFalse(result);
        verify(taxiService, never()).bookTaxi(any(), any());
        verify(bookingService, never()).confirmBooking(any(), any());
        verify(dashboardService, never()).updateStats(any(), any(), any());
    }

    @Test
    void testIsEligibleToServeBooking_True() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setAvailable(true);
        taxi.setStatus(TaxiStatus.AVAILABLE);
        taxi.setFromLatitude(12.9716);
        taxi.setFromLongitude(77.5946);

        StoredBooking booking = new StoredBooking();
        booking.setStatus(BookingStatus.PENDING);
        booking.setFromLatitude(12.9716);
        booking.setFromLongitude(77.5946);

        boolean result = strategy.isEligibleToServeBooking(taxi, booking);

        assertTrue(result);
    }

    @Test
    void testIsEligibleToServeBooking_False() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setAvailable(false);
        taxi.setStatus(TaxiStatus.BOOKED);
        taxi.setFromLatitude(12.9716);
        taxi.setFromLongitude(77.5946);

        StoredBooking booking = new StoredBooking();
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setFromLatitude(12.9716);
        booking.setFromLongitude(77.5946);

        boolean result = strategy.isEligibleToServeBooking(taxi, booking);

        assertFalse(result);
    }

    @Test
    void testIsNearBy_True() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setAvailable(true);
        taxi.setStatus(TaxiStatus.AVAILABLE);
        taxi.setFromLatitude(12.9716);
        taxi.setFromLongitude(77.5946);

        StoredBooking booking = new StoredBooking();
        booking.setFromLatitude(12.9716);
        booking.setFromLongitude(77.5946);
        booking.setStatus(BookingStatus.PENDING);

        boolean result = strategy.isNearBy(booking, taxi);

        assertTrue(result);
    }

    @Test
    void testIsNearBy_False() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setFromLatitude(12.9716);
        taxi.setFromLongitude(77.5946);

        StoredBooking booking = new StoredBooking();
        booking.setFromLatitude(13.0358);
        booking.setFromLongitude(77.5970);

        boolean result = strategy.isNearBy(booking, taxi);

        assertFalse(result);
    }
}