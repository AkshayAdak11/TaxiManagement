//package com.taxifleet.resources;
//
//
//import com.taxifleet.db.StoredDashboard;
//import com.taxifleet.security.JwtAuthFilter;
//import com.taxifleet.security.JwtTokenService;
//import com.taxifleet.services.DashboardService;
//import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
//import io.dropwizard.testing.junit5.ResourceExtension;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.Response;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(DropwizardExtensionsSupport.class)
//class DashboardResourceTest {
//
//    private static final DashboardService dashboardService = mock(DashboardService.class);
//    private static final JwtTokenService jwtTokenService = new JwtTokenService();
//    private static final ResourceExtension RESOURCES = ResourceExtension.builder()
//            .addResource(new DashboardResource(dashboardService))
//            .addProvider(new JwtAuthFilter())
//            .build();
//
//    private String validToken;
//
//    @BeforeEach
//    void setUp() {
//        validToken = jwtTokenService.generateToken("testUser");
//    }
//
//    @Test
//    void testGetDashboardStatsWithValidToken() {
//        StoredDashboard dashboard = new StoredDashboard(10, 5, 3);
//        when(dashboardService.getLatestDashboardStats()).thenReturn(dashboard);
//
//        Response response = RESOURCES.target("/dashboard/stats")
//                .request()
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
//                .get();
//
//        assertEquals(200, response.getStatus());
//        StoredDashboard responseDashboard = response.readEntity(StoredDashboard.class);
//        assertEquals(dashboard, responseDashboard);
//    }
//
//    @Test
//    void testGetDashboardStatsWithoutToken() {
//        Response response = RESOURCES.target("/dashboard/stats")
//                .request()
//                .get();
//
//        assertEquals(401, response.getStatus());
//    }
//
//    @Test
//    void testGetDashboardStatsWithInvalidToken() {
//        Response response = RESOURCES.target("/dashboard/stats")
//                .request()
//                .header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken")
//                .get();
//
//        assertEquals(401, response.getStatus());
//    }
//}