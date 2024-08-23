package com.taxifleet.db;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "dashboard")
public class StoredDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false, unique = true)
    private long bookingId;

    @Column(name = "taxi_number")
    private String taxiNumber;

    // Start and end time of the booking
    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    // Booking status
    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "is_pending", nullable = false)
    private boolean isPending;

    @Column(name = "is_cancelled", nullable = false)
    private boolean isCancelled;

    @Column(name = "is_expired", nullable = false)
    private boolean isExpired;

    // Fare for the booking
    @Column(name = "fare")
    private double fare;

    // Location data of the booking
    @Column(name = "booking_latitude")
    private double bookingLatitude;

    @Column(name = "booking_longitude")
    private double bookingLongitude;

    @Version
    @Column(name = "version")
    private Long version;
}