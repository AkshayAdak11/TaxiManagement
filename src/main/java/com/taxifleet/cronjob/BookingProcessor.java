package com.taxifleet.cronjob;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.DashboardService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BookingProcessor {
    private final BookingService bookingService;
    private final ScheduledExecutorService schedulerService;
    private final ExecutorService pendingWorkerService;
    private final DashboardService dashboardService;

    public BookingProcessor(BookingService bookingService,
                            DashboardService dashboardService) {
        this.bookingService = bookingService;
        this.dashboardService = dashboardService;
        this.schedulerService = Executors.newScheduledThreadPool(10); //We have kept the scheduler thread separate
        this.pendingWorkerService = Executors.newFixedThreadPool(20); // We have kept pending worker thread separate
    }

    public void startProcessing() {
        schedulerService.scheduleAtFixedRate(() -> {
            List<StoredBooking> pendingBookings = bookingService.allPendingBooking();
            for (StoredBooking booking : pendingBookings) {
                pendingWorkerService.submit(new BookingTask(bookingService, booking, dashboardService));
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void shutdown() {
        schedulerService.shutdown();
        pendingWorkerService.shutdown();
        try {
            if (!schedulerService.awaitTermination(60, TimeUnit.SECONDS)) {
                schedulerService.shutdownNow();
            }
            if (!pendingWorkerService.awaitTermination(60, TimeUnit.SECONDS)) {
                pendingWorkerService.shutdownNow();
            }
        } catch (InterruptedException e) {
            schedulerService.shutdownNow();
            pendingWorkerService.shutdownNow();
        }
    }
}