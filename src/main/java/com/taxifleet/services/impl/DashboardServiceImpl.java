package com.taxifleet.services.impl;

import com.taxifleet.db.StoredDashboard;
import com.taxifleet.db.dao.DashboardDAO;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.services.DashboardService;
import io.dropwizard.hibernate.UnitOfWork;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class DashboardServiceImpl implements DashboardService {

    private final DashboardDAO dashboardDAO;
    private final BlockingQueue<StoredDashboard> dashboardQueue = new LinkedBlockingQueue<>();

    @Inject
    public DashboardServiceImpl(DashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
    }

    @Transactional
    public void updateDashboardStats(BookingStatus status) {
        // Fetch the existing dashboard record
        StoredDashboard dashboard = dashboardDAO.getAllDashboards();

        if (dashboard == null) {
            dashboard = new StoredDashboard();
            dashboard.setTotalBookings(1);
            dashboard.setTotalCompletedBookings(0);
            dashboard.setTotalPendingBookings(1);

        } else {

            long totalCompletedBookings = getTotalCompletedBookings();
            long totalPendingBookings = getTotalPendingBookings();

            // Update statistics based on the booking status
            if (BookingStatus.PENDING.equals(status)) {
                totalPendingBookings++;
            } else if (BookingStatus.COMPLETED.equals(status)) {
                totalCompletedBookings++;
                totalPendingBookings--;
            } else if (BookingStatus.CANCELLED.equals(status)) {
                totalPendingBookings--;
            }

            long totalBookings = totalPendingBookings + totalCompletedBookings;

            dashboard.setTotalBookings(totalBookings);
            dashboard.setTotalCompletedBookings(totalCompletedBookings);
            dashboard.setTotalPendingBookings(totalPendingBookings);
        }

        // Save or update the dashboard record in the database
        dashboardDAO.saveOrUpdateDashboard(dashboard);
        if (!dashboardQueue.isEmpty()) {
            dashboardQueue.remove();
        }
        dashboardQueue.offer(dashboard);
    }

    @UnitOfWork
    public long getTotalBookings() {
        return dashboardDAO.getAllDashboards().getTotalBookings();
    }

    @UnitOfWork
    public long getTotalCompletedBookings() {
        return dashboardDAO.getAllDashboards().getTotalCompletedBookings();
    }

    @UnitOfWork
    public long getTotalPendingBookings() {
        return dashboardDAO.getAllDashboards().getTotalPendingBookings();
    }

    public StoredDashboard getLatestDashboardStats() {
        return dashboardQueue.peek();
    }
}