//package com.taxifleet.cronjob;
//
//import com.taxifleet.db.StoredBooking;
//import com.taxifleet.enums.BookingStatus;
//import com.taxifleet.services.BookingService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.util.concurrent.TimeUnit;
//
//import static org.mockito.Mockito.*;
//
//public class BookingTaskTest {
//    private BookingService bookingService;
//    private StoredBooking booking;
//    private BookingTask bookingTask;
//
//    @BeforeEach
//    public void setUp() {
//        bookingService = Mockito.mock(BookingService.class);
//        booking = new StoredBooking();
//        booking.setStatus(BookingStatus.PENDING);
//        bookingTask = new BookingTask(bookingService, booking);
//    }
//
//    @Test
//    public void testRunWithRetry() throws InterruptedException {
//        doThrow(new RuntimeException()).when(bookingService).publishBooking(booking);
//
//        bookingTask.run();
//        TimeUnit.SECONDS.sleep(1);
//
//        verify(bookingService, atLeastOnce()).publishBooking(booking);
//    }
//
//    @Test
//    public void testRunWithCancel() throws InterruptedException {
//        booking.setStatus(BookingStatus.CANCELLED);
//
//        bookingTask.run();
//        TimeUnit.SECONDS.sleep(1);
//
//        verify(bookingService, never()).publishBooking(booking);
//        verify(bookingService, atLeastOnce()).cancelBooking(booking);
//    }
//}