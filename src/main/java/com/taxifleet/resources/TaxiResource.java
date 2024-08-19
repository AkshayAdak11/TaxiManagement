package com.taxifleet.resources;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStrategy;
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
@Tag(name = "Taxis Api")
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
    @Path("/{id}")
    @Operation(summary = "Returns a taxi by its ID")
    @UnitOfWork
    public StoredTaxi getTaxi(
            @ApiParam(value = "ID of the taxi to fetch", required = true) @PathParam("id") Long id) {
        return cachedTaxiService.getTaxi(id);
    }

    @POST
    @Operation(summary =  "Creates a new taxi and returns the created taxi")
    @UnitOfWork
    public StoredTaxi createTaxi(
            @ApiParam(value = "Add Taxi ", required = true) StoredTaxi taxi) {
        return cachedTaxiService.createTaxi(taxi);
    }

    @PUT
    @Path("/{id}/availability")
    @Operation(summary =  "Sets the availability of a taxi by its ID")
    @UnitOfWork
    public void setTaxiAvailability(
            @ApiParam(value = "Get Taxi By ID", required = true) @PathParam("id") Long id,
            @ApiParam(value = "Availability status to set", required = true) @QueryParam("available") boolean available) {
        cachedTaxiService.setTaxiAvailability(id, available);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary =  "Deletes a taxi by its ID")
    @UnitOfWork
    public void deleteTaxi(
            @ApiParam(value = "ID of the taxi to delete", required = true) @PathParam("id") Long id) {
        cachedTaxiService.deleteTaxi(id);
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


//    @GET
//    @Path("/nearby/booking")
//    @UnitOfWork
//    public Response getNearbyTaxis(@QueryParam("bookinId") String bookingId,
//                                   @QueryParam("") Double longitude,
//                                   @QueryParam("radius") Double radius) {
//        List<StoredTaxi> taxis = cachedTaxiService.findNearbyTaxis(latitude, longitude, radius);
//        return Response.ok(taxis).build();
//    }


    @POST
    @Path("/{id}/subscribe")
    @Operation(summary = "Subscribe a taxi to booking notifications with a chosen strategy")
    @UnitOfWork
    public Response subscribeTaxi(
            @ApiParam(value = "Taxi ID", required = true) @PathParam("id") Long taxiId,
            @ApiParam(value = "Strategy Type", required = true) @QueryParam("strategy") BookingStrategy strategyType) {

        boolean subscribed = cachedTaxiService.subscribeTaxiToBookings(taxiId, strategyType);
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
    @Path("/{id}/unsubscribe")
    public Response unsubscribeFromBookings(@PathParam("id") Long taxiId) {
        boolean subscribed = cachedTaxiService.unsubscribeTaxiToBookings(taxiId);
        if (subscribed) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/{id}/select-booking")
    @Operation(summary = "Select a booking for the taxi")
    @UnitOfWork
    public Response selectBooking(
            @ApiParam(value = "Taxi ID", required = true) @PathParam("id") Long taxiId,
            @ApiParam(value = "Booking ID", required = true) @QueryParam("bookingId") Long bookingId) {

        StoredBooking storedBooking = cachedTaxiService.getBookingTaxis(bookingId);
        TaxiObserver observer = cachedTaxiService.getTaxiObserver(taxiId);

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
}