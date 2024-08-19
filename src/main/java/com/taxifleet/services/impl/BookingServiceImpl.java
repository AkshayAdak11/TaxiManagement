package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.repository.BookingRepository;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.MessagingService;

import javax.inject.Inject;
import java.util.List;

public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final MessagingService messagingService;

    @Inject
    public BookingServiceImpl(BookingRepository bookingRepository,
                              MessagingService messagingService) {
        this.bookingRepository = bookingRepository;
        this.messagingService = messagingService;
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
    public synchronized void confirmBooking(StoredBooking storedBooking, String taxiId) {
        storedBooking.setStatus(BookingStatus.COMPLETED);
        storedBooking.setTaxiId(taxiId);
        bookingRepository.updateBooking(storedBooking);
    }


    @Override
    public List<StoredBooking> allPendingBooking() {
        return bookingRepository.findAllPendingBookings();
    }
}