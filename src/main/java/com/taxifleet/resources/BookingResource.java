package com.taxifleet.resources;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.services.BookingService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Booking Api")
public class BookingResource {

    private final BookingService bookingService;

    @Inject
    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GET
    @UnitOfWork
    @Operation(summary = "Returns a list of all bookings")
    public List<com.taxifleet.db.StoredBooking> getBookings() {
        return bookingService.getBookings();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Returns a booking by its ID")
    public com.taxifleet.db.StoredBooking getBooking(
            @ApiParam(value = "ID of the booking to fetch", required = true) @PathParam("id") Long id) {
        return bookingService.getBooking(id);
    }

    @POST

    @Operation(summary = "Creates a new booking and returns the created booking")
    @Path("/create")
    @UnitOfWork
    public com.taxifleet.db.StoredBooking createBooking(
            @ApiParam(value = "Booking creation", required = true) StoredBooking storedBooking) {
        com.taxifleet.db.StoredBooking newStoredBooking = bookingService.createBooking(storedBooking);
        bookingService.publishBooking(storedBooking);
        return newStoredBooking;
    }

    @DELETE
    @UnitOfWork
    @Path("/{id}")
    @Operation(summary = "Deletes a booking by its ID")
    public void deleteBooking(
            @ApiParam(value = "ID of the booking to delete", required = true) @PathParam("id") Long id) {
        bookingService.deleteBooking(id);
    }
}