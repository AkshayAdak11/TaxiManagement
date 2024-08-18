//package com.taxifleet.services.impl;
//import com.taxifleet.enums.BookingStatus;
//import com.taxifleet.model.BookingTaxis;
//import com.taxifleet.services.BookingService;
//import com.taxifleet.services.CachedTaxiService;
//import com.taxifleet.services.DashboardService;
//import com.taxifleet.services.impl.MessagingServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import static org.mockito.Mockito.*;
//
//class MessagingServiceImplTest {
//
//    @Mock
//    private CachedTaxiService cachedTaxiService;
//
//    @Mock
//    private BookingService bookingService;
//
//    @Mock
//    private DashboardService dashboardService;
//
//    @InjectMocks
//    private MessagingServiceImpl messagingService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testPublishBookingPending() {
//        BookingTaxis bookingTaxis = new BookingTaxis();
//        bookingTaxis.setPublishToAllTaxis(true);
//
//        messagingService.publishBooking(bookingTaxis);
//
//        // Ensure the executor service has time to execute the task
//        verify(dashboardService, timeout(1000).times(1)).updateDashboardStats(BookingStatus.PENDING);
//    }
//
//    @Test
//    void testPublishBookingCompleted() {
//        BookingTaxis bookingTaxis = new BookingTaxis();
//        bookingTaxis.setPublishToAllTaxis(true);
//        when(cachedTaxiService.isTaxiAvailable()).thenReturn(true);
//
//        messagingService.publishBooking(bookingTaxis);
//
//        verify(dashboardService, times(1)).updateDashboardStats(BookingStatus.COMPLETED);
//    }
//
//    @Test
//    void testPublishBookingCancelled() {
//        BookingTaxis bookingTaxis = new BookingTaxis();
//        bookingTaxis.setPublishToAllTaxis(true);
//        when(cachedTaxiService.isTaxiAvailable()).thenReturn(false);
//
//        messagingService.publishBooking(bookingTaxis);
//
//        verify(dashboardService, timeout(1000).times(1)).updateDashboardStats(BookingStatus.CANCELLED);
//    }
//}