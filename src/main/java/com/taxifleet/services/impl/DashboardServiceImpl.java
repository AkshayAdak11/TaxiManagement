package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredDashboard;
import com.taxifleet.db.dao.DashboardDAO;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.DashboardService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;
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
        startConsumer();  // Start the consumer thread
    }


    //CHANGE NAME
    public void createBookingInitialStats(StoredDashboard storedDashboard) {
        dashboardQueue.offer(storedDashboard);
    }

    public void updateDashboardStats(StoredBooking booking, String taxiNumber, BookingStatus status) {
        boolean pending = BookingStatus.PENDING.equals(status);
        boolean completed = BookingStatus.COMPLETED.equals(status);
        boolean cancelled = BookingStatus.CANCELLED.equals(status);
        boolean expired = BookingStatus.EXPIRED.equals(status);
        StoredDashboard storedDashboard = new StoredDashboard();
        storedDashboard.setBookingId(booking.getBookingId());
        storedDashboard.setTaxiNumber(taxiNumber);
        storedDashboard.setPending(pending);
        storedDashboard.setCompleted(completed);
        storedDashboard.setCancelled(cancelled);
        storedDashboard.setExpired(expired);
        storedDashboard.setStartTime(booking.getStartTime());
        storedDashboard.setEndTime(booking.getEndTime());
        storedDashboard.setBookingLatitude(booking.getFromLatitude());
        storedDashboard.setBookingLongitude(booking.getFromLongitude());
        createBookingInitialStats(storedDashboard);
    }

    /**
     * Starts a separate thread to consume and process dashboard updates.
     */
    private void startConsumer() {
        Thread consumerThread = new Thread(() -> {
            while (true) {
                try {
                    StoredDashboard storedDashboard = dashboardQueue.take();
                    processDashboardUpdate(storedDashboard);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // Restore interrupted state
                    break;
                }
            }
        });
        consumerThread.setDaemon(true);  // Set as a daemon thread so it doesn't block application shutdown
        consumerThread.start();
    }

    /**
     * Process the dashboard update.
     */
    private void processDashboardUpdate(StoredDashboard updatedStoredDashboard) {
        StoredDashboard storedDashboard = dashboardDAO.findByBookingId(updatedStoredDashboard.getBookingId());
        if (Objects.nonNull(storedDashboard)) {
            storedDashboard.setCancelled(updatedStoredDashboard.isCancelled());
            storedDashboard.setCompleted(updatedStoredDashboard.isCompleted());
            storedDashboard.setPending(updatedStoredDashboard.isPending());
            storedDashboard.setExpired(updatedStoredDashboard.isExpired());
            storedDashboard.setTaxiNumber(updatedStoredDashboard.getTaxiNumber());
            storedDashboard.setStartTime(updatedStoredDashboard.getStartTime());
            storedDashboard.setEndTime(updatedStoredDashboard.getEndTime());
            storedDashboard.setBookingLongitude(updatedStoredDashboard.getBookingLongitude());
            storedDashboard.setBookingLongitude(updatedStoredDashboard.getBookingLongitude());
            storedDashboard.setBookingLatitude(updatedStoredDashboard.getBookingLatitude());
            dashboardDAO.saveOrUpdateDashboard(storedDashboard);
            return;
        }
        dashboardDAO.saveOrUpdateDashboard(updatedStoredDashboard);

    }

    public List<StoredDashboard> getLatestDashboardStats() {
        return dashboardDAO.getAllDashboards();
    }

    public List<StoredDashboard> findByTimeRange(Date startTime, Date endTime) {
        return dashboardDAO.findByTimeRange(startTime, endTime);
    }

    public List<StoredDashboard> findByLocationRange(double fromLatitude, double fromLongitude) {
        return dashboardDAO.findByLocationRange(fromLatitude, fromLongitude);
    }

    @Override
    public List<StoredDashboard> getAllBookingsForTaxi(String taxiNumber) {
        return dashboardDAO.findAllBookingsByTaxiId(taxiNumber);
    }
    //lat long
}
