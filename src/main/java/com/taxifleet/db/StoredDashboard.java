package com.taxifleet.db;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dashboard")
public class StoredDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_bookings")
    private long totalBookings;

    @Column(name = "total_completed_bookings")
    private long totalCompletedBookings;

    @Column(name = "total_pending_bookings")
    private long totalPendingBookings;

}