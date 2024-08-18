package com.taxifleet.db;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.model.Location;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "stored_booking")
public class StoredBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", unique = true)
    private Long bookingId;

    @Column(name = "taxi_id")
    private String taxId;


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
}