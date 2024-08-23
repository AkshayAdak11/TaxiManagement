package com.taxifleet.resources;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredDashboard;
import com.taxifleet.services.DashboardService;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Path("/dashboard")
@Tag(name = "Dashboard", description = "Dashboard for Statistics")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    private final DashboardService dashboardService;

    @Inject
    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GET
    @Path("/booking/stats")
    @Operation(description = "Get all stats for Booking state")
    public Response getDashboardStats() {
        List<StoredDashboard> dashboard = dashboardService.getLatestDashboardStats();
        return Response.ok(computeBookingStats(dashboard)).build();
    }

    @GET
    @Path("/all/bookings")
    @Operation(description = "Get all stats of Booking")
    public Response getAllBooking() {
        List<StoredDashboard> storedDashboardList = dashboardService.getLatestDashboardStats();
        return Response.ok(storedDashboardList).build();
    }


    @GET
    @Path("/stats/{taxiNumber}")
    @Operation(description = "Get all stats of Booking for taxis")
    public Response getAllStatsForTaxi(
            @ApiParam(required = true) @PathParam("taxiNumber") String taxiNumber) {
        List<StoredDashboard> getAllBookingsForTaxi = dashboardService.getAllBookingsForTaxi(taxiNumber);
        return Response.ok(getAllBookingsForTaxi).build();
    }


    @GET
    @Path("/stats/{taxiNumber}/bookings")
    @Operation(description = "Get all stats of Booking for taxis")
    public Response getAllBookingsByTaxiNumber(
            @ApiParam(required = true) @PathParam("taxiNumber") String taxiNumber) {
        List<StoredDashboard> getAllBookingsForTaxi = dashboardService.getAllBookingsForTaxi(taxiNumber);
        Map<String, Object> bookingStats = computeBookingStats(getAllBookingsForTaxi);
        return Response.ok(bookingStats).build();
    }


    @GET
    @Path("/stats/location/bookings")
    @Operation(description = "Get all stats of Booking for taxis")
    public Response getAllBookingsForTimeRange(
            @ApiParam(required = true) @QueryParam("startTime") Date startTime,
            @ApiParam(required = true) @QueryParam("endTime") Date endTime) {
        List<StoredDashboard> bookings = dashboardService.findByTimeRange(startTime, endTime);
        Map<String, Object> statsMap = computeBookingStats(bookings);
        return Response.ok(statsMap).build();
    }


    @GET
    @Path("/stats/time/bookings")
    @Operation(description = "Get all stats of Booking for taxis")
    public Response getAllBookingsForLocation(
            @ApiParam(required = true) @PathParam("bookingLatitude") double bookingLatitude,
            @ApiParam(required = true) @PathParam("bookingLongitude") double bookingLongitude) {
        List<StoredDashboard> bookings = dashboardService.findByLocationRange(bookingLatitude, bookingLongitude);
        Map<String, Object> statsMap = computeBookingStats(bookings);
        return Response.ok(statsMap).build();
    }


    private Map<String, Object> computeBookingStats(List<StoredDashboard> bookings) {
        long pending = 0;
        long completed = 0;
        long cancelled = 0;
        long expired = 0;
        double totalFare = 0.0;
        Map<Long, String> bookingIds = new HashMap<>();
        for (StoredDashboard storedDashboard : bookings) {
            if (storedDashboard.isCompleted()) {
                completed++;
            }
            if (storedDashboard.isCancelled()) {
                cancelled++;
            }
            if (storedDashboard.isPending()) {
                pending++;
            }
            if (storedDashboard.isExpired()) {
                expired++;
            }
            totalFare += storedDashboard.getFare();
            bookingIds.put(storedDashboard.getBookingId(), storedDashboard.getTaxiNumber());
        }

        long totalBookings = completed + cancelled + pending;

        Map<String, Object> statsMap = new HashMap<>();
        statsMap.put("TotalBookings", totalBookings);
        statsMap.put("Completed", completed);
        statsMap.put("Pending", pending);
        statsMap.put("Cancelled", cancelled);
        statsMap.put("Expired", expired);
        statsMap.put("TotalFare", totalFare);
        statsMap.put("AllBookingIds", bookingIds);

        return statsMap;
    }
}