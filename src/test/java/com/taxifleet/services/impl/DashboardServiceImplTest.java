//package com.taxifleet.services.impl;
//
//
//import com.taxifleet.db.StoredDashboard;
//import com.taxifleet.db.dao.DashboardDAO;
//import com.taxifleet.enums.BookingStatus;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class DashboardServiceImplTest {
//
//    @Mock
//    private DashboardDAO dashboardDAO;
//
//    @InjectMocks
//    private DashboardServiceImpl dashboardService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testUpdateDashboardStatsPending() {
//        when(dashboardDAO.getAllDashboards()).thenReturn(Collections.emptyList());
//
//        dashboardService.updateDashboardStats(BookingStatus.PENDING);
//
//        verify(dashboardDAO, times(1)).saveOrUpdateDashboard(any(StoredDashboard.class));
//        assertEquals(1, dashboardService.getLatestDashboardStats().getTotalPendingBookings());
//    }
//
//    @Test
//    void testUpdateDashboardStatsCompleted() {
//        when(dashboardDAO.getAllDashboards()).thenReturn(Collections.emptyList());
//
//        dashboardService.updateDashboardStats(BookingStatus.COMPLETED);
//
//        verify(dashboardDAO, times(1)).saveOrUpdateDashboard(any(StoredDashboard.class));
//        assertEquals(1, dashboardService.getLatestDashboardStats().getTotalCompletedBookings());
//    }
//
//    @Test
//    void testUpdateDashboardStatsCancelled() {
//        when(dashboardDAO.getAllDashboards()).thenReturn(Collections.emptyList());
//
//        dashboardService.updateDashboardStats(BookingStatus.CANCELLED);
//
//        verify(dashboardDAO, times(1)).saveOrUpdateDashboard(any(StoredDashboard.class));
//        assertEquals(-1, dashboardService.getLatestDashboardStats().getTotalPendingBookings());
//    }
//
//    @Test
//    void testGetLatestDashboardStats() {
//        StoredDashboard dashboard = new StoredDashboard(10, 5, 3);
//        when(dashboardDAO.getAllDashboards()).thenReturn(Collections.singletonList(dashboard));
//
//        dashboardService.updateDashboardStats(BookingStatus.PENDING);
//        dashboardService.updateDashboardStats(BookingStatus.COMPLETED);
//
//        StoredDashboard latestDashboard = dashboardService.getLatestDashboardStats();
//
//        assertNotNull(latestDashboard);
//        assertEquals(11, latestDashboard.getTotalBookings());
//        assertEquals(6, latestDashboard.getTotalCompletedBookings());
//        assertEquals(4, latestDashboard.getTotalPendingBookings());
//    }
//}