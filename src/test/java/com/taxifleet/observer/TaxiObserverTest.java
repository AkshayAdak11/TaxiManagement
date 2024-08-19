package com.taxifleet.observer;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.services.CentralizedBookingService;
import com.taxifleet.strategy.BookingAssignmentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaxiObserverTest {
    private StoredTaxi taxi;
    private BookingAssignmentStrategy assignmentStrategy;
    private CentralizedBookingService centralizedBookingService;
    private TaxiObserver taxiObserver;

    @BeforeEach
    public void setUp() {
        taxi = Mockito.mock(StoredTaxi.class);
        assignmentStrategy = Mockito.mock(BookingAssignmentStrategy.class);
        centralizedBookingService = Mockito.mock(CentralizedBookingService.class);
        taxiObserver = new TaxiObserver(taxi, assignmentStrategy, centralizedBookingService);
    }

    @Test
    public void testUpdate() {
        StoredBooking booking = new StoredBooking();
        when(assignmentStrategy.isEligibleToServeBooking(taxi, booking)).thenReturn(true);

        taxiObserver.update(booking);
        assertTrue(taxiObserver.getAvailableBookings().containsKey(booking));
    }

    @Test
    public void testSelectBooking() {
        StoredBooking booking = new StoredBooking();
        booking.setStatus(BookingStatus.PENDING);
        taxiObserver.getAvailableBookings().put(booking, true);

        when(centralizedBookingService.assignBookingToTaxi(taxi, booking)).thenReturn(true);
        when(assignmentStrategy.assignBooking(taxi, booking)).thenReturn(true);
        when(taxi.isAvailable()).thenReturn(true);
        when(taxi.getStatus()).thenReturn(TaxiStatus.AVAILABLE);

        boolean result = taxiObserver.selectBooking(booking);
        assertTrue(result);
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
        verify(centralizedBookingService).notifyObserversBookingCompleted(booking);
        verify(centralizedBookingService).removeBookingFromAssignment(booking);
    }

    @Test
    public void testRemoveBooking() {
        StoredBooking booking = new StoredBooking();
        taxiObserver.getAvailableBookings().put(booking, true);

        taxiObserver.removeBooking(booking);
        assertFalse(taxiObserver.getAvailableBookings().containsKey(booking));
    }
}