package com.taxifleet.resources;

import com.google.common.base.Preconditions;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.TaxiService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/taxis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Taxis Api", description = "Taxi Related Api")
public class TaxiResource {

    private final TaxiService taxiService;
    private final BookingService bookingService;

    @Inject
    public TaxiResource(TaxiService taxiService,
                        BookingService bookingService) {
        this.taxiService = taxiService;
        this.bookingService = bookingService;
    }

    @GET
    @Operation(summary = "Returns a list of all taxis")
    @UnitOfWork
    public List<StoredTaxi> getAllTaxis() {
        return taxiService.getAllTaxis();
    }

    @GET
    @Path("/{taxiNumber}")
    @Operation(summary = "Returns a taxi by its ID")
    @UnitOfWork
    public StoredTaxi getTaxi(
            @ApiParam(value = "ID of the taxi to fetch", required = true) @PathParam("taxiNumber") String taxiNumber) {
        return taxiService.getTaxi(taxiNumber);
    }

    @POST
    @Operation(summary = "Creates a new taxi and returns the created taxi")
    @UnitOfWork
    public StoredTaxi createTaxi(
            @ApiParam(value = "Add Taxi ", required = true) StoredTaxi taxi) {
        Preconditions.checkArgument(TaxiStatus.BOOKED != taxi.getStatus());
        return taxiService.createTaxi(taxi);
    }

    @PUT
    @Path("/{taxiNumber}/{availabilityStatus}")
    @Operation(summary = "Sets the availability of a taxi by its ID")
    @UnitOfWork
    public void updateTaxiAvailability(
            @ApiParam(value = "Get Taxi By ID", required = true) @PathParam("taxiNumber") String taxiNumber,
            @ApiParam(value = "Availability status to set", required = true) @QueryParam("available") boolean available,
            @ApiParam(value = "Availability status", required = true) @PathParam("availabilityStatus") TaxiStatus taxiStatus) {
        taxiService.updateTaxiAvailability(taxiNumber, available, taxiStatus);
    }

    @DELETE
    @Path("/{taxiNumber}")
    @Operation(summary = "Deletes a taxi by its ID")
    @UnitOfWork
    public void deleteTaxi(
            @ApiParam(value = "ID of the taxi to delete", required = true) @PathParam("taxiNumber") String taxiNumber) {
        taxiService.deleteTaxi(taxiNumber);
    }

    @GET
    @Path("/nearby")
    @UnitOfWork
    public Response getNearbyTaxis(@QueryParam("latitude") Double latitude,
                                   @QueryParam("longitude") Double longitude,
                                   @QueryParam("radius") Double radius) {
        List<StoredTaxi> taxis = taxiService.findNearbyTaxis(latitude, longitude, radius);
        return Response.ok(taxis).build();
    }


    @GET
    @Path("/all/booking/assigned")
    @UnitOfWork
    public Response getAssignedBookingAsPerChoice(@QueryParam("taxiNumber") String taxiNumber) {
        List<StoredBooking> storedBookings = bookingService.getAllBookingsForTaxi(taxiNumber);
        return Response.ok()
                .entity(storedBookings)
                .build();
    }

    @POST
    @Path("/{taxiNumber}/unsubscribe")
    public Response unsubscribeFromBookings(@PathParam("taxiNumber") String taxiNumber) {
        boolean subscribed = taxiService.unsubscribeTaxi(taxiNumber);
        if (subscribed) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/{taxiNumber}/{bookingId}/select-booking")
    @Operation(summary = "Select a booking for the taxi")
    @UnitOfWork
    public Response selectBooking(
            @ApiParam(value = "Taxi ID", required = true) @PathParam("taxiNumber") String taxiNumber,
            @ApiParam(value = "Booking ID", required = true) @PathParam("bookingId") Long bookingId) {

        return taxiService.selectBooking(taxiNumber, bookingId);
    }

    @GET
    @Path("/all/subscribed/taxis")
    @Operation(summary = "Get all subscribed taxis")
    @RolesAllowed("admin")
    public Response getAllSubscribedTaxis() {
        return taxiService.getAllSubscribedTaxis();
    }
}