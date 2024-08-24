package com.taxifleet.strategy;

import static org.junit.jupiter.api.Assertions.*;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllDistanceBasedAssignmentStrategyTest {

    private TaxiService taxiService;
    private BookingService bookingService;
    private DashboardService dashboardService;
    private AllDistanceBasedAssignmentStrategy strategy;

    @BeforeEach
    void setUp() {
        taxiService = mock(TaxiService.class);
        bookingService = mock(BookingService.class);
        dashboardService = mock(DashboardService.class);
        strategy = new AllDistanceBasedAssignmentStrategy(taxiService, bookingService, dashboardService);
    }

    @Test
    void testAssignBooking_Success() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setAvailable(true);
        taxi.setStatus(TaxiStatus.AVAILABLE);

        StoredBooking booking = new StoredBooking();
        booking.setBookingId(1L);

        when(taxiService.bookTaxi(taxi, booking)).thenReturn(true);

        boolean result = strategy.assignBooking(taxi, booking);

        assertTrue(result);
        assertFalse(taxi.isAvailable());
        assertEquals(TaxiStatus.BOOKED, taxi.getStatus());
        verify(bookingService).confirmBooking(booking, taxi.getTaxiNumber());
        verify(dashboardService).updateStats(booking, taxi.getTaxiNumber(), BookingStatus.COMPLETED);
    }

    @Test
    void testAssignBooking_Failure() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setAvailable(false);
        taxi.setStatus(TaxiStatus.BOOKED);

        StoredBooking booking = new StoredBooking();
        booking.setBookingId(1L);

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

        StoredBooking booking = new StoredBooking();
        booking.setStatus(BookingStatus.PENDING);

        boolean result = strategy.isEligibleToServeBooking(taxi, booking);

        assertTrue(result);
    }

    @Test
    void testIsEligibleToServeBooking_False() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setAvailable(false);
        taxi.setStatus(TaxiStatus.BOOKED);

        StoredBooking booking = new StoredBooking();
        booking.setStatus(BookingStatus.COMPLETED);

        boolean result = strategy.isEligibleToServeBooking(taxi, booking);

        assertFalse(result);
    }
}