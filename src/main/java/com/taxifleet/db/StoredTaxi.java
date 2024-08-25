package com.taxifleet.db;

import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.enums.TaxiStatus;
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

@Data
@Entity
@Table(name = "taxis")
public class StoredTaxi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "taxi_number", unique = true, nullable = false)
    private String taxiNumber;

    @Column(name = "available")
    private boolean available;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaxiStatus status;

    @Column(name = "current_latitude", nullable = false)
    private double currentLatitude;

    @Column(name = "current_longitude", nullable = false)
    private double currentLongitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "bookingStrategy")
    private BookingStrategy bookingStrategy;

    @Version
    @Column(name = "version")
    private Long version;
}