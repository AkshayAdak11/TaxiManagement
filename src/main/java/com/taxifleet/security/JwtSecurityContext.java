package com.taxifleet.security;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class JwtSecurityContext implements SecurityContext {

    private final Principal principal;

    public JwtSecurityContext(Principal principal) {
        this.principal = principal;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        // Implement role check logic
        return false;
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return "JWT";
    }
}
