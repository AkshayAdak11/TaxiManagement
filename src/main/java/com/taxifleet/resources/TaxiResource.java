package com.taxifleet.resources;

import com.google.common.base.Preconditions;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.observer.TaxiObserver;
import com.taxifleet.services.CachedTaxiService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/taxis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Taxis Api", description = "Taxi Related Api")
public class TaxiResource {

    private final CachedTaxiService cachedTaxiService;

    @Inject
    public TaxiResource(CachedTaxiService cachedTaxiService) {
        this.cachedTaxiService = cachedTaxiService;
    }

    @GET
    @Operation(summary = "Returns a list of all taxis")
    @UnitOfWork
    public List<StoredTaxi> getAllTaxis() {
        return cachedTaxiService.getAllTaxis();
    }

    @GET
    @Path("/{taxiNumber}")
    @Operation(summary = "Returns a taxi by its ID")
    @UnitOfWork
    public StoredTaxi getTaxi(
            @ApiParam(value = "ID of the taxi to fetch", required = true) @PathParam("taxiNumber") String taxiNumber) {
        return cachedTaxiService.getTaxi(taxiNumber);
    }

    @POST
    @Operation(summary = "Creates a new taxi and returns the created taxi")
    @UnitOfWork
    public StoredTaxi createTaxi(
            @ApiParam(value = "Add Taxi ", required = true) StoredTaxi taxi) {
        Preconditions.checkArgument(taxi.getFromLatitude() == taxi.getToLatitude() &&
                taxi.getFromLongitude() == taxi.getToLongitude());
        return cachedTaxiService.createTaxi(taxi);
    }

    @PUT
    @Path("/{taxiNumber}/{availabilityStatus}")
    @Operation(summary = "Sets the availability of a taxi by its ID")
    @UnitOfWork
    public void updateTaxiAvailability(
            @ApiParam(value = "Get Taxi By ID", required = true) @PathParam("taxiNumber") String taxiNumber,
            @ApiParam(value = "Availability status to set", required = true) @QueryParam("available") boolean available,
            @ApiParam(value = "Availability status", required = true) @PathParam("availabilityStatus") TaxiStatus taxiStatus) {
        cachedTaxiService.updateTaxiAvailability(taxiNumber, available, taxiStatus);
    }

    @DELETE
    @Path("/{taxiNumber}")
    @Operation(summary = "Deletes a taxi by its ID")
    @UnitOfWork
    public void deleteTaxi(
            @ApiParam(value = "ID of the taxi to delete", required = true) @PathParam("taxiNumber") String taxiNumber) {
        cachedTaxiService.deleteTaxi(taxiNumber);
    }

    @GET
    @Path("/nearby")
    @UnitOfWork
    public Response getNearbyTaxis(@QueryParam("latitude") Double latitude,
                                   @QueryParam("longitude") Double longitude,
                                   @QueryParam("radius") Double radius) {
        List<StoredTaxi> taxis = cachedTaxiService.findNearbyTaxis(latitude, longitude, radius);
        return Response.ok(taxis).build();
    }


    @GET
    @Path("/all/booking/assigned")
    @UnitOfWork
    public Response getAssignedBookingAsPerChoice(@QueryParam("taxiNumber") String taxiNumber) {
        List<StoredBooking> storedBookings = cachedTaxiService.getAllBookingsAsPerChoice(taxiNumber);
        return Response.ok()
                .entity(storedBookings)
                .build();
    }


    @POST
    @Path("/{taxiNumber}/subscribe")
    @Operation(summary = "Subscribe a taxi to booking notifications with a chosen strategy")
    @UnitOfWork
    public Response subscribeTaxi(
            @ApiParam(value = "Taxi ID", required = true) @PathParam("taxiNumber") String taxiNumber,
            @ApiParam(value = "Strategy Type", required = true) @QueryParam("strategy") BookingStrategy strategyType) {

        boolean subscribed = cachedTaxiService.subscribeTaxiToBookings(taxiNumber, strategyType);
        if (subscribed) {
            return Response.ok()
                    .entity("Taxi subscribed successfully with strategy: " + strategyType)
                    .build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Failed to subscribe taxi with strategy: " + strategyType)
                    .build();
        }
    }

    @POST
    @Path("/{taxiNumber}/unsubscribe")
    public Response unsubscribeFromBookings(@PathParam("taxiNumber") String taxiNumber) {
        boolean subscribed = cachedTaxiService.unsubscribeTaxiToBookings(taxiNumber);
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
            @ApiParam(value = "Booking ID", required = true) @QueryParam("bookingId") Long bookingId) {

        StoredBooking storedBooking = cachedTaxiService.getBookingTaxis(bookingId);
        TaxiObserver observer = cachedTaxiService.getTaxiObserver(taxiNumber);

        if (observer != null && storedBooking != null) {
            boolean success = observer.selectBooking(storedBooking);
            if (success) {
                return Response.ok().entity("Booking selected successfully").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Failed to select booking").build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Taxi or booking not found").build();
        }
    }

    @GET
    @Path("/all/subscribed/taxis")
    public Response getAllSubscribedTaxis() {
        List<TaxiObserver> taxiObservers = cachedTaxiService.getAllTaxiObserver();
            return Response.ok().entity(taxiObservers).build();
        }
}