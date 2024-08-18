package com.taxifleet.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TaxiFleetExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        // Log the exception (optional)
        exception.printStackTrace();

        // Create a custom error response
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        errorResponse.setMessage("An unexpected error occurred: " + exception.getMessage());

        // Return the response with a proper HTTP status code
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }

    // Inner class to represent the error response structure
    public static class ErrorResponse {
        private int status;
        private String message;

        // Getters and setters
        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
