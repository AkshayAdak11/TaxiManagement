package com.taxifleet.services.impl;

import com.taxifleet.db.StoredTaxi;
import com.taxifleet.model.BookingTaxis;
import com.taxifleet.patterns.BookingObserver;
import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.MessagingService;
import com.taxifleet.services.BookingService;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

public class MessagingServiceImpl implements MessagingService {
    private final ConcurrentHashMap<Long, BookingTaxis> bookingMap = new ConcurrentHashMap<>();
    private final Set<BookingObserver> bookingObservers = new HashSet<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final CachedTaxiService cachedTaxiService;
    private final BookingService bookingService;

    @Inject
    public MessagingServiceImpl(CachedTaxiService cachedTaxiService, BookingService bookingService) {
        this.cachedTaxiService = cachedTaxiService;
        this.bookingService = bookingService;
    }

    public void addBookingObserver(BookingObserver observer) {
        bookingObservers.add(observer);
    }

    public void publishBooking(BookingTaxis bookingTaxis) {
        if (bookingTaxis.isPublishToAllTaxis()) {
            executorService.submit(() -> {
                bookingMap.put(bookingTaxis.getStoredBooking().getId(), bookingTaxis);
                System.out.println("Published booking to in-memory map: " + bookingTaxis);
                processBooking(bookingTaxis);
            });
        } else {
            executorService.submit(() -> {
                bookingMap.put(bookingTaxis.getStoredBooking().getId(), bookingTaxis);
                System.out.println("Published booking to in-memory map: " + bookingTaxis);
                processBookingNearByTaxis(bookingTaxis);
            });
        }
    }

    private void processBooking(BookingTaxis bookingTaxis) {
        if (cachedTaxiService.isTaxiAvailable()) {
            StoredTaxi taxi = cachedTaxiService.getTaxiAvailable();
            bookTaxi(taxi, bookingTaxis);
        } else {
            scheduleBookingCheck(bookingTaxis);
        }
    }

    private void processBookingNearByTaxis(BookingTaxis bookingTaxis) {
        StoredTaxi taxi = cachedTaxiService.findNearByAvailableTaxi(bookingTaxis.getStoredBooking().getLatitude(),
                bookingTaxis.getStoredBooking().getLongitude(), 10.00);
        if (Objects.nonNull(taxi)) {
            bookTaxi(taxi, bookingTaxis);
        } else {
            scheduleBookingCheck(bookingTaxis);
        }
    }

    private void bookTaxi(StoredTaxi taxi, BookingTaxis bookingTaxis) {
        cachedTaxiService.bookTaxi(taxi, bookingTaxis.getStoredBooking().getBookingId());
        bookingService.confirmBooking(bookingTaxis, taxi.getTaxiNumber());
        bookingMap.remove(bookingTaxis.getStoredBooking().getId());
        System.out.println("Taxi booked for booking: " + bookingTaxis);
    }

    private void scheduleBookingCheck(BookingTaxis bookingTaxis) {
        final ScheduledFuture<?>[] future = new ScheduledFuture<?>[1];
        future[0] = scheduler.scheduleAtFixedRate(() -> {
            if (cachedTaxiService.isTaxiAvailable()) {
                StoredTaxi taxi = cachedTaxiService.getTaxiAvailable();
                bookTaxi(taxi, bookingTaxis);
                future[0].cancel(false);
            }
        }, 0, 20, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            if (!future[0].isDone()) {
                future[0].cancel(false);
                cancelBooking(bookingTaxis);
            }
        }, 10, TimeUnit.MINUTES);
    }

    private void cancelBooking(BookingTaxis bookingTaxis) {
        bookingService.cancelBooking(bookingTaxis);
        bookingMap.remove(bookingTaxis.getStoredBooking().getId());
        System.out.println("Booking cancelled due to no available taxis: " + bookingTaxis);
    }
}