package com.taxifleet;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.taxifleet.module.BookingModule;
import com.taxifleet.module.HibernateModule;
import com.taxifleet.module.TaxiFleetModule;
import org.junit.Before;
import java.util.Arrays;
import java.util.List;


public abstract class BaseIntegrationTest extends BaseTest{
    @Before
    public void baseSetup() {
        List<AbstractModule> abstractModules =
                Arrays.asList(new TaxiFleetModule(), new HibernateModule(hibernateBundle), new BookingModule());
        Injector injector = Guice.createInjector(abstractModules);
        injector.injectMembers(this);
    }

}