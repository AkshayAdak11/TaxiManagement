package com.taxifleet.services.impl;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.model.BookingTaxis;
import com.taxifleet.repository.BookingRepository;
import com.taxifleet.repository.TaxiRepository;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.MessagingService;

import javax.inject.Inject;
import javax.transaction.Transactional;
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
    public void publishBooking(BookingTaxis bookingTaxis) {
        messagingService.publishBooking(bookingTaxis);
    }


    @Override
    public List<StoredBooking> getBookings() {
        return bookingRepository.getAllBookings();
    }

    @Override
    public StoredBooking getBooking(Long id) {
        return bookingRepository.getBooking(id);
    }

    @Override
    public StoredBooking createBooking(BookingTaxis bookingTaxis) {
        return bookingRepository.createBooking(bookingTaxis.getStoredBooking());
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteBooking(id);
    }

    @Override
    public void cancelBooking(BookingTaxis bookingTaxis) {
        StoredBooking booking = bookingRepository.getBooking(bookingTaxis.getStoredBooking().getId());
        if (booking != null) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.updateBooking(booking);
        }
    }

    @Override
    public void confirmBooking(BookingTaxis bookingTaxis, String taxiId) {
        StoredBooking storedBooking = bookingTaxis.getStoredBooking();
        storedBooking.setStatus(BookingStatus.COMPLETED);
        storedBooking.setTaxId(taxiId);
        bookingRepository.updateBooking(storedBooking);
    }
}