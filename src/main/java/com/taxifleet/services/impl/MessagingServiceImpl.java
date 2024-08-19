package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.MessagingService;
import com.taxifleet.services.BookingService;
import com.taxifleet.strategy.AllDistanceBasedAssignmentStrategy;
import com.taxifleet.strategy.BookingAssignmentStrategy;
import com.taxifleet.strategy.DistanceBasedAssignmentStrategy;

import javax.inject.Inject;
import java.util.Deque;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class MessagingServiceImpl implements MessagingService {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Deque<StoredBooking> bookingQueue = new LinkedBlockingDeque<>();

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

    public void publishBooking(StoredBooking storedBooking) {
        bookingQueue.offer(storedBooking); // Add the booking to the queue (end of the queue)
        executorService.submit(this::processNextBooking); // Trigger processing
        publishBookingPending();
        System.out.println("Published booking: " + storedBooking);
    }

    private void processNextBooking() {
        StoredBooking nextBooking = bookingQueue.poll(); // Retrieve and remove the next booking from the queue (front of the queue)
        if (nextBooking != null) {
            processBooking(nextBooking);
        }
    }

    private void processBooking(StoredBooking storedBooking) {
        if (cachedTaxiService.isTaxiAvailable()) {
            StoredTaxi taxi = cachedTaxiService.getTaxiAvailable(); // Get an available taxi
            if (taxi != null) {
                boolean bookingConfirmed = bookTaxi(taxi, storedBooking);
                if (!bookingConfirmed) {
                    reAddBookingToFront(storedBooking);
                }
            } else {
                scheduleBookingCheck(storedBooking);
            }
        } else {
            scheduleBookingCheck(storedBooking);
        }
    }

    private boolean bookTaxi(StoredTaxi taxi, StoredBooking storedBooking) {
        boolean bookingSuccessful = cachedTaxiService.bookTaxi(taxi, storedBooking.getBookingId());
        if (bookingSuccessful) {
            bookingService.confirmBooking(storedBooking, taxi.getTaxiNumber());
            System.out.println("Taxi booked for booking: " + storedBooking);
            publishBookingCompleted();
            processNextBooking(); // Process the next booking after this one is complete
            return true;
        } else {
            System.out.println("Taxi rejected booking: " + storedBooking);
            return false;
        }
    }

    private void reAddBookingToFront(StoredBooking storedBooking) {
        bookingQueue.offerFirst(storedBooking); // Add the booking back to the front of the queue
        System.out.println("Re-added booking to the front of the queue: " + storedBooking);
    }

    private void scheduleBookingCheck(StoredBooking storedBooking) {
        final AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();

        futureRef.set(scheduler.scheduleWithFixedDelay(() -> {
            try {
                if (cachedTaxiService.isTaxiAvailable()) {
                    StoredTaxi taxi = cachedTaxiService.getTaxiAvailable(); // Get an available taxi
                    if (taxi != null) {
                        boolean bookingConfirmed = bookTaxi(taxi, storedBooking);
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
                    cancelBooking(storedBooking); // Cancel the booking due to timeout
                }
            } catch (Exception e) {

                System.err.println("Error during booking cancellation: " + e.getMessage());
            }
        }, 10, TimeUnit.MINUTES);
    }

    private void cancelBooking(StoredBooking storedBooking) {
        bookingService.cancelBooking(storedBooking);
        System.out.println("Booking cancelled due to no available taxis: " + storedBooking);
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


    @Override
    public void notifyTaxis(StoredBooking storedBooking) {
        cachedTaxiService.notifyTaxis(storedBooking);  // Notify taxis directly
    }

    @Override
    public BookingAssignmentStrategy createStrategy(BookingStrategy bookingStrategy) {
        return switch (bookingStrategy) {
            case NEAR_BY -> new DistanceBasedAssignmentStrategy(10, cachedTaxiService, bookingService);
            case ALL_AREA -> new AllDistanceBasedAssignmentStrategy(cachedTaxiService, bookingService);
            default -> throw new IllegalArgumentException("Unknown strategy type: " + bookingStrategy);
        };
    }
}
