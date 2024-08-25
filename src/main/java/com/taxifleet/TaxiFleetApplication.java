package com.taxifleet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.taxifleet.cronjob.BookingProcessor;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredDashboard;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.health.TaxiFleetHealthCheck;
import com.taxifleet.module.HibernateModule;
import com.taxifleet.module.TaxiFleetModule;
import com.taxifleet.resources.BookingResource;
import com.taxifleet.resources.DashboardResource;
import com.taxifleet.resources.JWTResource;
import com.taxifleet.resources.TaxiResource;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.utils.TaxiFleetExceptionMapper;
import in.vectorpro.dropwizard.swagger.SwaggerBundle;
import in.vectorpro.dropwizard.swagger.SwaggerBundleConfiguration;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TaxiFleetApplication extends Application<TaxiFleetConfiguration> {

    private Injector injector;

    private final HibernateBundle<TaxiFleetConfiguration> hibernateBundle =
            new HibernateBundle<TaxiFleetConfiguration>(StoredTaxi.class, StoredBooking.class, StoredDashboard.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(TaxiFleetConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    public static void main(String[] args) throws Exception {
        new TaxiFleetApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TaxiFleetConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new SwaggerBundle<TaxiFleetConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(TaxiFleetConfiguration configuration) {
                return configuration.getSwagger();
            }
        });
    }

    @Override
    public void run(TaxiFleetConfiguration configuration, Environment environment) {
        injector = Guice.createInjector(new TaxiFleetModule(),
                new HibernateModule(hibernateBundle));

        final TaxiResource taxiResource = injector.getInstance(TaxiResource.class);
        final BookingResource bookingResource = injector.getInstance(BookingResource.class);
        final DashboardResource dashboardResource = injector.getInstance(DashboardResource.class);
        final TaxiFleetHealthCheck healthCheck = injector.getInstance(TaxiFleetHealthCheck.class);
        final JWTResource jwtResource = injector.getInstance(JWTResource.class);

        environment.healthChecks().register("taxiFleet", healthCheck);
        environment.jersey().register(taxiResource);
        environment.jersey().register(bookingResource);
        environment.jersey().register(dashboardResource);
        environment.jersey().register(jwtResource);
        environment.jersey().register(injector.getInstance(TaxiFleetExceptionMapper.class));
//        environment.jersey().register(RolesAllowedDynamicFeature.class);

        // Instantiate and start the BookingProcessor
        BookingService bookingService = injector.getInstance(BookingService.class);
        DashboardService dashboardService = injector.getInstance(DashboardService.class);
        BookingProcessor bookingProcessor = new BookingProcessor(bookingService, dashboardService);
        bookingProcessor.startProcessing();

        Runtime.getRuntime().addShutdownHook(new Thread(bookingProcessor::shutdown));
    }
}