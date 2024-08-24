package com.taxifleet.utils;

import org.junit.jupiter.api.Test;
import javax.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

class TaxiFleetExceptionMapperTest {

    @Test
    void testToResponse() {
        TaxiFleetExceptionMapper exceptionMapper = new TaxiFleetExceptionMapper();
        Exception exception = new Exception("Test exception");

        Response response = exceptionMapper.toResponse(exception);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        TaxiFleetExceptionMapper.ErrorResponse errorResponse = (TaxiFleetExceptionMapper.ErrorResponse) response.getEntity();
        assertNotNull(errorResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorResponse.getStatus());
        assertEquals("An unexpected error occurred: Test exception", errorResponse.getMessage());
    }
}