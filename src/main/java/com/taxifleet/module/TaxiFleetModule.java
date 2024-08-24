package com.taxifleet.module;

import com.google.inject.AbstractModule;
import com.taxifleet.repository.TaxiRepository;
import com.taxifleet.repository.impl.TaxiRepositoryImpl;
import com.taxifleet.resources.BookingResource;
import com.taxifleet.resources.DashboardResource;
import com.taxifleet.resources.TaxiResource;
import com.taxifleet.services.BookingAssignmentService;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.TaxiService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.MessagingService;
import com.taxifleet.services.impl.BookingServiceImpl;
import com.taxifleet.services.impl.CachedTaxiServiceImpl;
import com.taxifleet.services.impl.BookingAssignmentServiceImpl;
import com.taxifleet.services.impl.DashboardServiceImpl;
import com.taxifleet.services.impl.MessagingServiceImpl;

public class TaxiFleetModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaxiResource.class).asEagerSingleton();
        bind(BookingResource.class).asEagerSingleton();
        bind(DashboardResource.class).asEagerSingleton();
        bind(TaxiRepository.class).to(TaxiRepositoryImpl.class);
        bind(TaxiService.class).to(CachedTaxiServiceImpl.class);
        bind(MessagingService.class).to(MessagingServiceImpl.class);
        bind(BookingService.class).to(BookingServiceImpl.class);
        bind(DashboardService.class).to(DashboardServiceImpl.class);
        bind(BookingAssignmentService.class).to(BookingAssignmentServiceImpl.class);
    }
}