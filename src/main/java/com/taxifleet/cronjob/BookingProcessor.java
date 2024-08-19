package com.taxifleet.cronjob;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.services.BookingService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BookingProcessor {
    private final BookingService bookingService;
    private final ScheduledExecutorService executorService;

    public BookingProcessor(BookingService bookingService) {
        this.bookingService = bookingService;
        this.executorService = Executors.newScheduledThreadPool(20);
    }

    public void startProcessing() {
        executorService.scheduleAtFixedRate(() -> {
            List<StoredBooking> pendingBookings = bookingService.allPendingBooking();
            for (StoredBooking booking : pendingBookings) {
                executorService.submit(new BookingTask(bookingService, booking));
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}