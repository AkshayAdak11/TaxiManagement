package com.taxifleet.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.taxifleet.TaxiFleetConfiguration;
import io.dropwizard.hibernate.HibernateBundle;
import org.hibernate.SessionFactory;

public class HibernateModule extends AbstractModule {

    private final HibernateBundle<TaxiFleetConfiguration> hibernateBundle;

    public HibernateModule(HibernateBundle<TaxiFleetConfiguration> hibernateBundle) {
        this.hibernateBundle = hibernateBundle;
    }

    @Override
    protected void configure() {
        // Additional bindings can be configured here if needed
    }

    @Provides
    @Singleton
    public SessionFactory provideSessionFactory() {
        return hibernateBundle.getSessionFactory();
    }
}