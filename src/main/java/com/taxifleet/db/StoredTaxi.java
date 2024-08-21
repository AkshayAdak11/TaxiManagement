package com.taxifleet.db;

import com.taxifleet.enums.TaxiStatus;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "taxis")
public class StoredTaxi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "taxi_number", unique = true, nullable = false)
    private String taxiNumber;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "available")
    private boolean available;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaxiStatus status;

    @Column(name = "from_latitude", nullable = false)
    private double fromLatitude;

    @Column(name = "from_longitude", nullable = false)
    private double fromLongitude;

    @Column(name = "to_latitude", nullable = false)
    private double toLatitude;

    @Column(name = "to_longitude", nullable = false)
    private double toLongitude;

    @Version
    @Column(name = "version")
    private Long version;
}