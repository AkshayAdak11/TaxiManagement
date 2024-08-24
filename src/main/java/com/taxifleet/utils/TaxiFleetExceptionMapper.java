package com.taxifleet.utils;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TaxiFleetExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace();


        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        errorResponse.setMessage("An unexpected error occurred: " + exception.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }


    @Setter
    @Getter
    public static class ErrorResponse {
        private int status;
        private String message;
    }
}
