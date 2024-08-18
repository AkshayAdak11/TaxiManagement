package com.taxifleet.module;

import com.google.inject.AbstractModule;
import com.taxifleet.repository.BookingRepository;
import com.taxifleet.repository.impl.BookingRepositoryImpl;


public class BookingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BookingRepository.class).to(BookingRepositoryImpl.class);
    }
}
