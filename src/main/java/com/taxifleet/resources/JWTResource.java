package com.taxifleet.resources;

import com.taxifleet.utils.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/jwt")
@Tag(name = "JWT", description = "JWT Resource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JWTResource {

    @GET
    @Path("/generate/{subject}/{role}")
    @Operation(description = "Generate JWT token")
    public Response generateToken(@PathParam("subject") String subject, @PathParam("role") String role) {
        String token = JWTUtils.generateToken(subject, role);
        return Response.ok(token).build();
    }

    @GET
    @Path("/roles/{token}")
    @Operation(description = "Extract roles from JWT token")
    public Response extractRoles(@PathParam("token") String token) {
        try {
            String roles = JWTUtils.extractRoles(token);
            return Response.ok(roles).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build();
        }
    }
}