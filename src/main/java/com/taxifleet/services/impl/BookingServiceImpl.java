package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredDashboard;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.observer.TaxisObserver;
import com.taxifleet.repository.BookingRepository;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.MessagingService;

import javax.inject.Inject;
import java.util.List;

public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final MessagingService messagingService;

    private final DashboardService dashboardService;

    private final TaxisObserver taxisObserver;

    @Inject
    public BookingServiceImpl(BookingRepository bookingRepository,
                              MessagingService messagingService,
                              DashboardService dashboardService,
                              TaxisObserver taxisObserver) {
        this.bookingRepository = bookingRepository;
        this.messagingService = messagingService;
        this.dashboardService = dashboardService;
        this.taxisObserver = taxisObserver;
    }

    @Override
    public void publishBooking(StoredBooking storedBooking) {
        messagingService.notifyTaxis(storedBooking);
    }


    @Override
    public List<StoredBooking> getBookings() {
        return bookingRepository.getAllBookings();
    }

    @Override
    public StoredBooking getBooking(long id) {
        return bookingRepository.getBooking(id);
    }

    @Override
    public StoredBooking createBooking(StoredBooking storedBooking) {
        StoredDashboard storedDashboard  = new StoredDashboard();
        storedDashboard.setBookingId(storedBooking.getBookingId());
        storedDashboard.setBookingLatitude(storedBooking.getFromLatitude());
        storedDashboard.setBookingLongitude(storedBooking.getFromLongitude());
        storedDashboard.setEndTime(storedBooking.getEndTime());
        storedDashboard.setStartTime(storedBooking.getStartTime());
        storedDashboard.setPending(true);
        storedDashboard.setFare(storedBooking.getFare());
        dashboardService.publishStats(storedDashboard);
        return bookingRepository.createBooking(storedBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteBooking(id);
    }

    @Override
    public void cancelBooking(StoredBooking storedBooking) {
        StoredBooking booking = bookingRepository.getBooking(storedBooking.getBookingId());
        if (booking != null) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.updateBooking(booking);
        }
    }

    @Override
    public void expiredBooking(StoredBooking storedBooking) {
        StoredBooking booking = bookingRepository.getBooking(storedBooking.getBookingId());
        if (booking != null) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.updateBooking(booking);
        }
    }

    @Override
    public void confirmBooking(StoredBooking storedBooking, String taxiId) {
        storedBooking.setStatus(BookingStatus.COMPLETED);
        storedBooking.setTaxiId(taxiId);
        bookingRepository.updateBooking(storedBooking);
    }


    @Override
    public List<StoredBooking> allPendingBooking() {
        return bookingRepository.findAllPendingBookings();
    }

    @Override
    public List<StoredBooking> getAllBookingsForTaxi(String taxiNumber) {
        return taxisObserver.getAllBookingsForTaxi(taxiNumber);
    }
}