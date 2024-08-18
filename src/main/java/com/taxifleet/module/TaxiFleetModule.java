package com.taxifleet.module;

import com.google.inject.AbstractModule;
import com.taxifleet.repository.TaxiRepository;
import com.taxifleet.repository.impl.TaxiRepositoryImpl;
import com.taxifleet.resources.BookingResource;
import com.taxifleet.resources.DashboardResource;
import com.taxifleet.resources.TaxiResource;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.CachedTaxiService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.MessagingService;
import com.taxifleet.services.impl.BookingServiceImpl;
import com.taxifleet.services.impl.CachedTaxiServiceImpl;
import com.taxifleet.services.impl.DashboardServiceImpl;
import com.taxifleet.services.impl.MessagingServiceImpl;

public class TaxiFleetModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaxiResource.class).asEagerSingleton();
        bind(BookingResource.class).asEagerSingleton();
        bind(DashboardResource.class).asEagerSingleton();
        bind(TaxiRepository.class).to(TaxiRepositoryImpl.class);
        bind(CachedTaxiService.class).to(CachedTaxiServiceImpl.class);
        bind(MessagingService.class).to(MessagingServiceImpl.class);
        bind(BookingService.class).to(BookingServiceImpl.class);
        bind(DashboardService.class).to(DashboardServiceImpl.class);
    }
}