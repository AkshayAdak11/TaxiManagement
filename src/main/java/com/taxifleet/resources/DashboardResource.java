package com.taxifleet.resources;

import com.taxifleet.db.StoredDashboard;
import com.taxifleet.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/dashboard")
@Tag(name = "Dashboard", description = "Dashboard for Statistics")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})

public class DashboardResource {

    private final DashboardService dashboardService;

    @Inject
    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GET
    @Path("/stats")
    @Operation(description = "Get all stats for Booking")
    public Response getDashboardStats() {
        StoredDashboard dashboard = dashboardService.getLatestDashboardStats();
        return Response.ok(dashboard).build();
    }
}