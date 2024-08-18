package com.taxifleet.services;

import com.taxifleet.db.StoredDashboard;
import com.taxifleet.enums.BookingStatus;

public interface DashboardService {
    void updateDashboardStats(BookingStatus status);
    StoredDashboard getLatestDashboardStats();
}