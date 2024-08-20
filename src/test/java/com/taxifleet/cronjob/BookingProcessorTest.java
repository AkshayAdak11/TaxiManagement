//package com.taxifleet.cronjob;
//
//import com.taxifleet.db.StoredBooking;
//import com.taxifleet.services.BookingService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static org.mockito.Mockito.*;
//
//public class BookingProcessorTest {
//    private BookingService bookingService;
//    private BookingProcessor bookingProcessor;
//
//    @BeforeEach
//    public void setUp() {
//        bookingService = Mockito.mock(BookingService.class);
//        bookingProcessor = new BookingProcessor(bookingService);
//    }
//
//    @AfterEach
//    public void tearDown() {
//        bookingProcessor.shutdown();
//    }
//
//    @Test
//    public void testStartProcessing() throws InterruptedException {
//        StoredBooking booking1 = new StoredBooking();
//        StoredBooking booking2 = new StoredBooking();
//        List<StoredBooking> pendingBookings = Arrays.asList(booking1, booking2);
//
//        when(bookingService.allPendingBooking()).thenReturn(pendingBookings);
//
//        bookingProcessor.startProcessing();
//        TimeUnit.SECONDS.sleep(2);
//
//        verify(bookingService, atLeastOnce()).allPendingBooking();
//        verify(bookingService, atLeastOnce()).publishBooking(booking1);
//        verify(bookingService, atLeastOnce()).publishBooking(booking2);
//    }
//}