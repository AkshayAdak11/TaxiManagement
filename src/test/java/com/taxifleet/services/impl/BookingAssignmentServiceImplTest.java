package com.taxifleet.services.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.observer.TaxisObserver;
import com.taxifleet.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class BookingAssignmentServiceImplTest {

    private TaxisObserver taxisObserver;
    private BookingAssignmentServiceImpl bookingAssignmentService;

    @BeforeEach
    void setUp() {
        taxisObserver = mock(TaxisObserver.class);
        bookingAssignmentService = new BookingAssignmentServiceImpl(taxisObserver);
    }

    @Test
    void testAssignBookingToTaxi_Success() {
        StoredTaxi taxi = new StoredTaxi();
        StoredBooking booking = new StoredBooking();
        booking.setBookingId(1L);

        boolean result = bookingAssignmentService.assignBookingToTaxi(taxi, booking);

        assertTrue(result);
    }

    @Test
    void testAssignBookingToTaxi_Failure() {
        StoredTaxi taxi = new StoredTaxi();
        taxi.setTaxiNumber("BOOKED_TAXI");
        StoredBooking booking = new StoredBooking();
        booking.setBookingId(1L);
        StoredTaxi taxi2 = new StoredTaxi();


        bookingAssignmentService.assignBookingToTaxi(taxi, booking);
        //Another taxi booking same booking
        boolean result = bookingAssignmentService.assignBookingToTaxi(taxi2, booking);

        assertFalse(result);
    }

    @Test
    void testNotifyObserversBookingCompleted() {
        StoredBooking booking = new StoredBooking();

        bookingAssignmentService.notifyObserversBookingCompleted(booking);

        verify(taxisObserver).getAllTaxiManager();
    }

    @Test
    void testRemoveBookingFromAssignment() {
        StoredBooking booking = new StoredBooking();
        booking.setBookingId(1L);

        bookingAssignmentService.assignBookingToTaxi(new StoredTaxi(), booking);
        bookingAssignmentService.removeBookingFromAssignment(booking);

        assertNull(bookingAssignmentService.getBookingAssignments().get(booking.getBookingId()));
    }
}