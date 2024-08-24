package com.taxifleet.services.impl;

import com.taxifleet.db.StoredDashboard;
import com.taxifleet.db.dao.DashboardDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DashboardServiceImplTest {

    @Mock
    private DashboardDAO dashboardDAO;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dashboardService = spy(new DashboardServiceImpl(dashboardDAO));
    }

    @Test
    void testProcessDashboardUpdate() {
        StoredDashboard updatedDashboard = new StoredDashboard();
        updatedDashboard.setBookingId(1L);
        updatedDashboard.setCompleted(true);

        StoredDashboard existingDashboard = new StoredDashboard();
        existingDashboard.setBookingId(1L);

        when(dashboardDAO.findByBookingId(1L)).thenReturn(existingDashboard);

        dashboardService.processDashboardUpdate(updatedDashboard);

        verify(dashboardDAO, times(1)).saveOrUpdateDashboard(existingDashboard);
        assertTrue(existingDashboard.isCompleted());
    }

    @Test
    void testGetLatestDashboardStats() {
        dashboardService.getLatestDashboardStats();
        verify(dashboardDAO, times(1)).getAllDashboards();
    }

    @Test
    void testFindByTimeRange() {
        Date start = new Date();
        Date end = new Date();
        dashboardService.findByTimeRange(start, end);
        verify(dashboardDAO, times(1)).findByTimeRange(start, end);
    }

    @Test
    void testFindByLocationRange() {
        dashboardService.findByLocationRange(10.0, 20.0, 30.0, 40.0);
        verify(dashboardDAO, times(1)).findByLocationRange(10.0, 20.0, 30.0, 40.0);
    }

    @Test
    void testGetAllBookingsForTaxi() {
        dashboardService.getAllBookingsForTaxi("taxi123");
        verify(dashboardDAO, times(1)).findAllBookingsByTaxiId("taxi123");
    }
}
