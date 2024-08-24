package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredDashboard;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.observer.TaxisObserver;
import com.taxifleet.repository.BookingRepository;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private MessagingService messagingService;

    @Mock
    private DashboardService dashboardService;

    @Mock
    private TaxisObserver taxisObserver;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublishBooking() {
        StoredBooking storedBooking = new StoredBooking();
        storedBooking.setBookingId(1L);

        bookingService.publishBooking(storedBooking);

        verify(messagingService, times(1)).notifyTaxis(storedBooking);
    }

    @Test
    void testGetBookings() {
        List<StoredBooking> bookings = Collections.singletonList(new StoredBooking());
        when(bookingRepository.getAllBookings()).thenReturn(bookings);

        List<StoredBooking> result = bookingService.getBookings();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).getAllBookings();
    }

    @Test
    void testGetBooking() {
        StoredBooking booking = new StoredBooking();
        when(bookingRepository.getBooking(1L)).thenReturn(booking);

        StoredBooking result = bookingService.getBooking(1L);

        assertNotNull(result);
        verify(bookingRepository, times(1)).getBooking(1L);
    }

    @Test
    void testCreateBooking() {
        StoredBooking storedBooking = new StoredBooking();
        storedBooking.setBookingId(1L);
        storedBooking.setFromLatitude(10.0);
        storedBooking.setFromLongitude(20.0);
        storedBooking.setStartTime(new Date());
        storedBooking.setEndTime(new Date());
        storedBooking.setFare(100.00);

        StoredDashboard storedDashboard = new StoredDashboard();
        storedDashboard.setBookingId(1L);
        storedDashboard.setBookingLatitude(10.0);
        storedDashboard.setBookingLongitude(20.0);
        storedDashboard.setStartTime(storedBooking.getStartTime());
        storedDashboard.setEndTime(storedBooking.getEndTime());
        storedDashboard.setPending(true);
        storedDashboard.setFare(100.0);

        when(bookingRepository.createBooking(storedBooking)).thenReturn(storedBooking);

        StoredBooking result = bookingService.createBooking(storedBooking);

        assertNotNull(result);
        verify(dashboardService, times(1)).publishStats(storedDashboard);
        verify(bookingRepository, times(1)).createBooking(storedBooking);
    }

    @Test
    void testDeleteBooking() {
        bookingService.deleteBooking(1L);

        verify(bookingRepository, times(1)).deleteBooking(1L);
    }

    @Test
    void testCancelBooking() {
        StoredBooking storedBooking = new StoredBooking();
        storedBooking.setBookingId(1L);
        storedBooking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.getBooking(1L)).thenReturn(storedBooking);

        bookingService.cancelBooking(storedBooking);

        assertEquals(BookingStatus.CANCELLED, storedBooking.getStatus());
        verify(bookingRepository, times(1)).updateBooking(storedBooking);
    }

    @Test
    void testExpiredBooking() {
        StoredBooking storedBooking = new StoredBooking();
        storedBooking.setBookingId(1L);
        storedBooking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.getBooking(1L)).thenReturn(storedBooking);

        bookingService.expiredBooking(storedBooking);

        assertEquals(BookingStatus.EXPIRED, storedBooking.getStatus());
        verify(bookingRepository, times(1)).updateBooking(storedBooking);
    }

    @Test
    void testConfirmBooking() {
        StoredBooking storedBooking = new StoredBooking();
        storedBooking.setBookingId(1L);
        storedBooking.setStatus(BookingStatus.PENDING);
        storedBooking.setTaxiId("taxi123");

        bookingService.confirmBooking(storedBooking, "taxi123");

        assertEquals(BookingStatus.COMPLETED, storedBooking.getStatus());
        assertEquals("taxi123", storedBooking.getTaxiId());
        verify(bookingRepository, times(1)).updateBooking(storedBooking);
    }

    @Test
    void testAllPendingBooking() {
        List<StoredBooking> pendingBookings = Collections.singletonList(new StoredBooking());
        when(bookingRepository.findAllPendingBookings()).thenReturn(pendingBookings);

        List<StoredBooking> result = bookingService.allPendingBooking();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findAllPendingBookings();
    }

    @Test
    void testGetAllBookingsForTaxi() {
        List<StoredBooking> bookingsForTaxi = Collections.singletonList(new StoredBooking());
        when(taxisObserver.getAllBookingsForTaxi("taxi123")).thenReturn(bookingsForTaxi);

        List<StoredBooking> result = bookingService.getAllBookingsForTaxi("taxi123");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taxisObserver, times(1)).getAllBookingsForTaxi("taxi123");
    }
}
