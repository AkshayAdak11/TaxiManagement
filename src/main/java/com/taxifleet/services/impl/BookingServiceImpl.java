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
//        messagingService.publishBooking(bookingTaxis);
        messagingService.notifyTaxis(storedBooking);
    }


    @Override
    public List<com.taxifleet.db.StoredBooking> getBookings() {
        return bookingRepository.getAllBookings();
    }

    @Override
    public com.taxifleet.db.StoredBooking getBooking(Long id) {
        return bookingRepository.getBooking(id);
    }

    @Override
    public com.taxifleet.db.StoredBooking createBooking(StoredBooking storedBooking) {
        return bookingRepository.createBooking(storedBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteBooking(id);
    }

    @Override
    public void cancelBooking(StoredBooking storedBooking) {
        com.taxifleet.db.StoredBooking booking = bookingRepository.getBooking(storedBooking.getId());
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
}