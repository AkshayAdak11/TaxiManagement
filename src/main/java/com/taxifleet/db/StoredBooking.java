package com.taxifleet.db;

import com.taxifleet.enums.BookingStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Date;
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


    @Column(name = "from_latitude", nullable = false)
    private double fromLatitude;

    @Column(name = "from_longitude", nullable = false)
    private double fromLongitude;

    @Column(name = "to_latitude", nullable = false)
    private double toLatitude;

    @Column(name = "to_longitude", nullable = false)
    private double toLongitude;

    @Column(name = "fare")
    private double fare; // This is in minutes

    // Start and end time of the booking
    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

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
        return Objects.equals(bookingId, that.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
}