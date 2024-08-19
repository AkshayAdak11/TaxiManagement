package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.dao.BookingDAO;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.repository.BookingRepository;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {
    private BookingDAO bookingDAO;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private MessagingService messagingService;
    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        bookingDAO = Mockito.mock(BookingDAO.class);
        bookingService = new BookingServiceImpl(bookingRepository, messagingService);
    }

    @Test
    public void testAllPendingBooking() {
        List<StoredBooking> bookings = List.of(new StoredBooking(), new StoredBooking());
        when(bookingDAO.findPendingBookings()).thenReturn(bookings);

        List<StoredBooking> result = bookingService.allPendingBooking();
        assertEquals(2, result.size());
    }

    @Test
    public void testPublishBooking() {
        StoredBooking booking = new StoredBooking();
        booking.setStatus(BookingStatus.PENDING);
        doNothing().when(bookingDAO).update(booking);

        bookingService.publishBooking(booking);
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
        verify(bookingDAO, times(1)).update(booking);
    }

    @Test
    public void testCancelBooking() {
        StoredBooking booking = new StoredBooking();
        doNothing().when(bookingDAO).update(booking);

        bookingService.cancelBooking(booking);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        verify(bookingDAO, times(1)).update(booking);
    }
}