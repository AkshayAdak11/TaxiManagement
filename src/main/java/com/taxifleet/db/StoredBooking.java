package com.taxifleet.db;
import com.taxifleet.enums.BookingStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@Table(name = "stored_booking")
public class StoredBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", unique = true, nullable = false)
    private Long bookingId;

    @Column(name = "taxi_id")
    private String taxiId;


    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoredBooking that = (StoredBooking) o;
        return Objects.equals(bookingId, that.bookingId) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
}