package com.taxifleet;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredDashboard;
import com.taxifleet.db.StoredTaxi;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.AdminEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Single instance and would be used in all test classes
public abstract class BaseTest {

    private final Bootstrap<?> bootstrap = mock(Bootstrap.class);
    private final HealthCheckRegistry healthChecks = mock(HealthCheckRegistry.class);
    private final JerseyEnvironment jerseyEnvironment = mock(JerseyEnvironment.class);
    private final LifecycleEnvironment lifecycleEnvironment = mock(LifecycleEnvironment.class);
    private final AdminEnvironment adminEnvironment = mock(AdminEnvironment.class);
    protected final Environment environment = mock(Environment.class);
    protected HibernateBundle<TaxiFleetConfiguration> hibernateBundle;

    @BeforeAll
    public void setupClass() {
        log.info("Initializing test class setup.");

        try {
            when(jerseyEnvironment.getResourceConfig()).thenReturn(new DropwizardResourceConfig());
            when(environment.jersey()).thenReturn(jerseyEnvironment);
            when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
            when(environment.healthChecks()).thenReturn(healthChecks);
            when(environment.admin()).thenReturn(adminEnvironment);
            when(bootstrap.getHealthCheckRegistry()).thenReturn(healthChecks);
            when(bootstrap.getObjectMapper()).thenReturn(new ObjectMapper());
        } catch (Exception e) {
            log.error("Exception during class setup: ", e);
            throw e;
        }
    }

    @BeforeEach
    public void initializeTestDB() throws Exception {
        log.info("Initializing test database...");

        try {
            hibernateBundle = new HibernateBundle<TaxiFleetConfiguration>(
                    StoredTaxi.class, StoredBooking.class, StoredDashboard.class) {
                @Override
                public PooledDataSourceFactory getDataSourceFactory(TaxiFleetConfiguration taxiFleetConfiguration) {
                    return taxiFleetConfiguration.getDataSourceFactory();
                }
            };
            log.info("Initializing Hibernate bundle with bootstrap...");
            hibernateBundle.initialize(bootstrap);

            log.info("Loading configuration from YAML...");
            String data = fixture("config/config.yml");
            TaxiFleetConfiguration taxiFleetConfiguration = Jackson.newObjectMapper(new YAMLFactory())
                    .readValue(data, TaxiFleetConfiguration.class);

            log.info("Running Hibernate bundle with the environment...");
            hibernateBundle.run(taxiFleetConfiguration, environment);

            log.info("Test database initialized successfully.");
        } catch (Exception e) {
            log.error("Exception during database initialization: ", e);
            throw e; // rethrow the exception to ensure test failure if something goes wrong
        }
    }
}
