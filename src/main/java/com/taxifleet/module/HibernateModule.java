package com.taxifleet.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.taxifleet.TaxiFleetConfiguration;
import com.taxifleet.db.dao.BookingDAO;
import com.taxifleet.db.dao.TaxiDAO;
import com.taxifleet.repository.BookingRepository;
import com.taxifleet.repository.TaxiRepository;
import io.dropwizard.hibernate.HibernateBundle;
import org.hibernate.SessionFactory;

public class HibernateModule extends AbstractModule {

    private final HibernateBundle<TaxiFleetConfiguration> hibernateBundle;

    public HibernateModule(HibernateBundle<TaxiFleetConfiguration> hibernateBundle) {
        this.hibernateBundle = hibernateBundle;
    }

    @Override
    protected void configure() {
        bind(TaxiRepository.class).to(TaxiDAO.class);
        bind(BookingRepository.class).to(BookingDAO.class);
        // Additional bindings can be configured here if needed
    }

    @Provides
    @Singleton
    public SessionFactory provideSessionFactory() {
        return hibernateBundle.getSessionFactory();
    }
}