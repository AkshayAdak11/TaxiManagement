package com.taxifleet.services.impl;

import com.taxifleet.db.StoredDashboard;
import com.taxifleet.db.dao.DashboardDAO;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.services.DashboardService;
import io.dropwizard.hibernate.UnitOfWork;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class DashboardServiceImpl implements DashboardService {

    private final DashboardDAO dashboardDAO;
    private final BlockingQueue<BookingStatus> dashboardQueue = new LinkedBlockingQueue<>();

    @Inject
    public DashboardServiceImpl(DashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
        startConsumer();  // Start the consumer thread
    }

    /**
     * Adds a dashboard update task to the queue.
     */
    public void updateDashboardStats(BookingStatus status) {
        dashboardQueue.offer(status);
    }

    /**
     * Starts a separate thread to consume and process dashboard updates.
     */
    private void startConsumer() {
        Thread consumerThread = new Thread(() -> {
            while (true) {
                try {
                    BookingStatus status = dashboardQueue.take();  // Blocks until an item is available
                    processDashboardUpdate(status);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // Restore interrupted state
                    break;  // Exit the loop if interrupted
                }
            }
        });
        consumerThread.setDaemon(true);  // Set as a daemon thread so it doesn't block application shutdown
        consumerThread.start();
    }

    /**
     * Process the dashboard update.
     */
    @Transactional
    private void processDashboardUpdate(BookingStatus status) {
        StoredDashboard dashboard = dashboardDAO.getAllDashboards();

        if (dashboard == null) {
            dashboard = new StoredDashboard();
            if (BookingStatus.PENDING.equals(status)) {
                dashboard.setTotalPendingBookings(1);
            }
            if (BookingStatus.COMPLETED.equals(status)) {
                dashboard.setTotalCompletedBookings(1);
            }

            dashboard.setTotalBookings(1);

        } else {
            long totalCompletedBookings = dashboard.getTotalCompletedBookings();
            long totalPendingBookings = dashboard.getTotalPendingBookings();

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

        dashboardDAO.saveOrUpdateDashboard(dashboard);
    }

    public StoredDashboard getLatestDashboardStats() {
        // Since updates are processed from the queue, we assume the DB reflects the latest state
        return dashboardDAO.getAllDashboards();
    }
}
