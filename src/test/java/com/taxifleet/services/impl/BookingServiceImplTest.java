//package com.taxifleet.services.impl;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.taxifleet.db.StoredBooking;
//import com.taxifleet.enums.BookingStatus;
//import com.taxifleet.model.BookingTaxis;
//import com.taxifleet.repository.BookingRepository;
//import com.taxifleet.services.MessagingService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import static org.mockito.Mockito.*;
//
//class BookingServiceImplTest {
//
//    @Mock
//    private BookingRepository bookingRepository;
//
//    @Mock
//    private MessagingService messagingService;
//
//    @InjectMocks
//    private BookingServiceImpl bookingService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testPublishBooking() {
//        BookingTaxis bookingTaxis = new BookingTaxis();
//        bookingService.publishBooking(bookingTaxis);
//        verify(messagingService, times(1)).publishBooking(bookingTaxis);
//    }
//
//    @Test
//    void testCreateBooking() {
//        BookingTaxis bookingTaxis = new BookingTaxis();
//        StoredBooking storedBooking = new StoredBooking();
//        bookingTaxis.setStoredBooking(storedBooking);
//        when(bookingRepository.createBooking(storedBooking)).thenReturn(storedBooking);
//
//        StoredBooking result = bookingService.createBooking(bookingTaxis);
//
//        assertEquals(storedBooking, result);
//        verify(bookingRepository, times(1)).createBooking(storedBooking);
//    }
//
//    @Test
//    void testCancelBooking() {
//        BookingTaxis bookingTaxis = new BookingTaxis();
//        StoredBooking storedBooking = new StoredBooking();
//        storedBooking.setId(1L);
//        bookingTaxis.setStoredBooking(storedBooking);
//        when(bookingRepository.getBooking(1L)).thenReturn(storedBooking);
//
//        bookingService.cancelBooking(bookingTaxis);
//
//        assertEquals(BookingStatus.CANCELLED, storedBooking.getStatus());
//        verify(bookingRepository, times(1)).updateBooking(storedBooking);
//    }
//
//    @Test
//    void testConfirmBooking() {
//        BookingTaxis bookingTaxis = new BookingTaxis();
//        StoredBooking storedBooking = new StoredBooking();
//        bookingTaxis.setStoredBooking(storedBooking);
//
//        bookingService.confirmBooking(bookingTaxis, "taxi123");
//
//        assertEquals(BookingStatus.COMPLETED, storedBooking.getStatus());
//        assertEquals("taxi123", storedBooking.getTaxId());
//        verify(bookingRepository, times(1)).updateBooking(storedBooking);
//    }
//}