package com.taxifleet.services.impl;

import com.taxifleet.db.StoredDashboard;
import com.taxifleet.db.dao.DashboardDAO;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.services.DashboardService;
import io.dropwizard.hibernate.UnitOfWork;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DashboardServiceImpl implements DashboardService {

    private final DashboardDAO dashboardDAO;
    private final BlockingQueue<StoredDashboard> dashboardQueue = new LinkedBlockingQueue<>();

    @Inject
    public DashboardServiceImpl(DashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
    }

    @Transactional
    public void updateDashboardStats(BookingStatus status) {
        long totalBookings = getTotalBookings();
        long totalCompletedBookings = getTotalCompletedBookings();
        long totalPendingBookings = getTotalPendingBookings();

        if (BookingStatus.PENDING.equals(status)) {
            totalPendingBookings++;
        } else if (BookingStatus.COMPLETED.equals(status)) {
            totalCompletedBookings++;
        } else if (BookingStatus.CANCELLED.equals(status)) {
            totalPendingBookings--;
        }

        StoredDashboard dashboard = new StoredDashboard(totalBookings, totalCompletedBookings, totalPendingBookings);
        dashboardDAO.saveOrUpdateDashboard(dashboard);
        dashboardQueue.offer(dashboard);
    }

    @UnitOfWork
    public long getTotalBookings() {
        return dashboardDAO.getAllDashboards().stream().mapToLong(StoredDashboard::getTotalBookings).sum();
    }

    @UnitOfWork
    public long getTotalCompletedBookings() {
        return dashboardDAO.getAllDashboards().stream().mapToLong(StoredDashboard::getTotalCompletedBookings).sum();
    }

    @UnitOfWork
    public long getTotalPendingBookings() {
        return dashboardDAO.getAllDashboards().stream().mapToLong(StoredDashboard::getTotalPendingBookings).sum();
    }

    public StoredDashboard getLatestDashboardStats() {
        return dashboardQueue.peek();
    }
}