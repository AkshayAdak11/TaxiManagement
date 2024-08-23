package com.taxifleet.db;
import com.taxifleet.enums.BookingStatus;
import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
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
    private long fare; // This is in minutes

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