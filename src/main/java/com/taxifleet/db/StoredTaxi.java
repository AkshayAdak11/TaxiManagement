package com.taxifleet.db;

import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.model.Location;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "taxis")
public class StoredTaxi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "taxi_number", unique = true)
    private String taxiNumber;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "available")
    private boolean available;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaxiStatus status;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Version
    @Column(name = "version")
    private Long version;
}