package com.taxifleet.db;

import javax.persistence.*;

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

    public StoredDashboard() {
    }

    public StoredDashboard(long totalBookings, long totalCompletedBookings, long totalPendingBookings) {
        this.totalBookings = totalBookings;
        this.totalCompletedBookings = totalCompletedBookings;
        this.totalPendingBookings = totalPendingBookings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getTotalCompletedBookings() {
        return totalCompletedBookings;
    }

    public void setTotalCompletedBookings(long totalCompletedBookings) {
        this.totalCompletedBookings = totalCompletedBookings;
    }

    public long getTotalPendingBookings() {
        return totalPendingBookings;
    }

    public void setTotalPendingBookings(long totalPendingBookings) {
        this.totalPendingBookings = totalPendingBookings;
    }
}