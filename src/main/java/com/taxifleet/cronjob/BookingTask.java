package com.taxifleet.cronjob;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.services.BookingService;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingTask implements Runnable {
    private final BookingService bookingService;
    private final StoredBooking booking;
    private final AtomicInteger retryCount = new AtomicInteger(0);

    public BookingTask(BookingService bookingService, StoredBooking booking) {
        this.bookingService = bookingService;
        this.booking = booking;
    }

    @Override
    public void run() {
        try {
            bookingService.publishBooking(booking);
            if (booking.getStatus() == BookingStatus.PENDING && retryCount.incrementAndGet() < 3) {
                Executors.newSingleThreadScheduledExecutor().schedule(this, 5, TimeUnit.MINUTES);
            } else if (retryCount.get() >= 3) {
                bookingService.cancelBooking(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}