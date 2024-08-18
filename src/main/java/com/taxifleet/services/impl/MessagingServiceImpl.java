package com.taxifleet.services.impl;

import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.model.BookingTaxis;
import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.MessagingService;
import com.taxifleet.services.BookingService;

import javax.inject.Inject;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class MessagingServiceImpl implements MessagingService {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Deque<BookingTaxis> bookingQueue = new LinkedBlockingDeque<>();

    private final CachedTaxiService cachedTaxiService;
    private final BookingService bookingService;
    private final DashboardService dashboardService;

    @Inject
    public MessagingServiceImpl(CachedTaxiService cachedTaxiService,
                                BookingService bookingService,
                                DashboardService dashboardService) {
        this.cachedTaxiService = cachedTaxiService;
        this.bookingService = bookingService;
        this.dashboardService = dashboardService;
    }

    public void publishBooking(BookingTaxis bookingTaxis) {
        bookingQueue.offer(bookingTaxis); // Add the booking to the queue (end of the queue)
        executorService.submit(this::processNextBooking); // Trigger processing
        publishBookingPending();
        System.out.println("Published booking: " + bookingTaxis);
    }

    private void processNextBooking() {
        BookingTaxis nextBooking = bookingQueue.poll(); // Retrieve and remove the next booking from the queue (front of the queue)
        if (nextBooking != null) {
            processBooking(nextBooking);
        }
    }

    private void processBooking(BookingTaxis bookingTaxis) {
        if (cachedTaxiService.isTaxiAvailable()) {
            StoredTaxi taxi = cachedTaxiService.getTaxiAvailable(); // Get an available taxi
            if (taxi != null) {
                boolean bookingConfirmed = bookTaxi(taxi, bookingTaxis);
                if (!bookingConfirmed) {
                    reAddBookingToFront(bookingTaxis); // Re-add to the front of the queue if not confirmed
                }
            } else {
                scheduleBookingCheck(bookingTaxis);
            }
        } else {
            scheduleBookingCheck(bookingTaxis);
        }
    }

    private boolean bookTaxi(StoredTaxi taxi, BookingTaxis bookingTaxis) {
        boolean bookingSuccessful = cachedTaxiService.bookTaxi(taxi, bookingTaxis.getStoredBooking().getBookingId());
        if (bookingSuccessful) {
            bookingService.confirmBooking(bookingTaxis, taxi.getTaxiNumber());
            System.out.println("Taxi booked for booking: " + bookingTaxis);
            publishBookingCompleted();
            processNextBooking(); // Process the next booking after this one is complete
            return true;
        } else {
            System.out.println("Taxi rejected booking: " + bookingTaxis);
            return false;
        }
    }

    private void reAddBookingToFront(BookingTaxis bookingTaxis) {
        bookingQueue.offerFirst(bookingTaxis); // Add the booking back to the front of the queue
        System.out.println("Re-added booking to the front of the queue: " + bookingTaxis);
    }

    private void scheduleBookingCheck(BookingTaxis bookingTaxis) {
        final AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();

        futureRef.set(scheduler.scheduleWithFixedDelay(() -> {
            try {
                if (cachedTaxiService.isTaxiAvailable()) {
                    StoredTaxi taxi = cachedTaxiService.getTaxiAvailable(); // Get an available taxi
                    if (taxi != null) {
                        boolean bookingConfirmed = bookTaxi(taxi, bookingTaxis);
                        if (bookingConfirmed) {
                            futureRef.get().cancel(false); // Cancel the repeating task after booking
                        }
                    }
                }
            } catch (Exception e) {
                // Log exception appropriately
                System.err.println("Error during taxi booking: " + e.getMessage());
            }
        }, 0, 20, TimeUnit.SECONDS));

        scheduler.schedule(() -> {
            try {
                ScheduledFuture<?> future = futureRef.get();
                if (future != null && !future.isDone()) {
                    future.cancel(false); // Cancel the repeating task
                    cancelBooking(bookingTaxis); // Cancel the booking due to timeout
                }
            } catch (Exception e) {
                // Log exception appropriately
                System.err.println("Error during booking cancellation: " + e.getMessage());
            }
        }, 10, TimeUnit.MINUTES);
    }

    private void cancelBooking(BookingTaxis bookingTaxis) {
        bookingService.cancelBooking(bookingTaxis);
        System.out.println("Booking cancelled due to no available taxis: " + bookingTaxis);
        publishBookingCancelled();
    }

    public void publishBookingCancelled() {
        dashboardService.updateDashboardStats(BookingStatus.CANCELLED);
    }

    public void publishBookingPending() {
        dashboardService.updateDashboardStats(BookingStatus.PENDING);
    }

    public void publishBookingCompleted() {
        dashboardService.updateDashboardStats(BookingStatus.COMPLETED);
    }
}
