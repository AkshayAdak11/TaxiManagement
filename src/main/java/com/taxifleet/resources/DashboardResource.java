package com.taxifleet.resources;

import com.taxifleet.services.DashboardService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Dashboard Api")
public class DashboardResource {

    private final DashboardService dashboardService;

    @Inject
    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GET
    @UnitOfWork
    @ApiOperation(value = "Get dashboard statistics", notes = "Returns statistics for the dashboard")
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalBookings", dashboardService.getTotalBookings());
        stats.put("totalCompletedBookings", dashboardService.getTotalCompletedBookings());
        stats.put("totalPendingBookings", dashboardService.getTotalPendingBookings());
        return stats;
    }
}