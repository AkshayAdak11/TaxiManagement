package com.taxifleet.db;

import com.taxifleet.enums.BookingStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoredBookingTest {
    @Test
    public void testEqualsAndHashCode() {
        StoredBooking booking1 = new StoredBooking();
        booking1.setId(1L);
        booking1.setBookingId(100L);
        booking1.setTaxiId("200");
        booking1.setLatitude(12.34);
        booking1.setLongitude(56.78);
        booking1.setStatus(BookingStatus.PENDING);

        StoredBooking booking2 = new StoredBooking();
        booking2.setId(1L);
        booking2.setBookingId(100L);
        booking2.setTaxiId("200L");
        booking2.setLatitude(12.34);
        booking2.setLongitude(56.78);
        booking2.setStatus(BookingStatus.PENDING);

        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }
}