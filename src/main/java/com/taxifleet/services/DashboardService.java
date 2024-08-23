package com.taxifleet.services;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredDashboard;
import com.taxifleet.enums.BookingStatus;

import java.util.Date;
import java.util.List;

public interface DashboardService {
    void publishStats(StoredDashboard storedDashboard);

    void updateStats(StoredBooking booking, String taxiNumber, BookingStatus status);

    List<StoredDashboard> getLatestDashboardStats();

    List<StoredDashboard> getAllBookingsForTaxi(String taxiNumber);

    List<StoredDashboard> findByTimeRange(Date startTime, Date endTime);

    List<StoredDashboard> findByLocationRange(double fromLatitude, double fromLongitude);
}