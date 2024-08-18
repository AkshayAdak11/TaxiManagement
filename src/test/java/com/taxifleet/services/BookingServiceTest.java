//package com.taxifleet.services;
//
//import com.taxifleet.db.StoredBooking;
//import com.taxifleet.model.Location;
//import com.taxifleet.model.Taxi;
//import com.taxifleet.patterns.DispatchStrategy;
//import com.taxifleet.repository.BookingRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class BookingServiceTest {
//
//    @Mock
//    private BookingRepository bookingRepository;
//
//    @Mock
//    private MessagingService messagingService;
//
//    @Mock
//    private DispatchStrategy dispatchStrategy;
//
//    @InjectMocks
//    private BookingService bookingService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testDispatchBookingWithValidStrategy() {
//        StoredBooking booking = new StoredBooking();
//        booking.setLocation(new Location(12.9716, 77.5946));
//        List<Taxi> expectedTaxis = Collections.singletonList(new Taxi(any(), any()));
//        when(dispatchStrategy.findAvailableTaxis(any(Location.class))).thenReturn(expectedTaxis);
//
//        List<Taxi> taxis = bookingService.dispatchBooking(booking);
//
//        assertEquals(expectedTaxis, taxis);
//        verify(dispatchStrategy, times(1)).findAvailableTaxis(any(Location.class));
//    }
//
//    @Test
//    void testDispatchBookingWithNullStrategy() {
//        bookingService.setDispatchStrategy(null);
//        StoredBooking booking = new StoredBooking();
//        booking.setLocation(new Location(12.9716, 77.5946));
//
//        Exception exception = assertThrows(IllegalStateException.class, () -> {
//            bookingService.dispatchBooking(booking);
//        });
//
//        assertEquals("Dispatch strategy is not set", exception.getMessage());
//    }
//
//    @Test
//    void testCreateBooking() {
//        StoredBooking booking = new StoredBooking();
//        when(bookingRepository.createBooking(any(StoredBooking.class))).thenReturn(booking);
//
//        StoredBooking createdBooking = bookingService.createBooking(booking);
//
//        assertEquals(booking, createdBooking);
//        verify(bookingRepository, times(1)).createBooking(any(StoredBooking.class));
//    }
//
//    @Test
//    void testGetBooking() {
//        StoredBooking booking = new StoredBooking();
//        when(bookingRepository.getBooking(anyLong())).thenReturn(booking);
//
//        StoredBooking retrievedBooking = bookingService.getBooking(1L);
//
//        assertEquals(booking, retrievedBooking);
//        verify(bookingRepository, times(1)).getBooking(anyLong());
//    }
//
//    @Test
//    void testGetBookings() {
//        List<StoredBooking> bookings = Collections.singletonList(new StoredBooking());
//        when(bookingRepository.getAllBookings()).thenReturn(bookings);
//
//        List<StoredBooking> retrievedBookings = bookingService.getBookings();
//
//        assertEquals(bookings, retrievedBookings);
//        verify(bookingRepository, times(1)).getAllBookings();
//    }
//
//    @Test
//    void testDeleteBooking() {
//        doNothing().when(bookingRepository).deleteBooking(anyLong());
//
//        bookingService.deleteBooking(1L);
//        verify(bookingRepository, times(1)).deleteBooking(anyLong());
//    }
//}