package com.taxifleet.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.db.dao.TaxiDAO;
import com.taxifleet.factory.TaxiObserverFactory;
import com.taxifleet.repository.TaxiRepository;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CachedTaxiServiceImplTest {
    private TaxiDAO taxiDAO;
    private Cache cacheManager;

    private CachedTaxiServiceImpl cachedTaxiService;
    @Mock
    private TaxiRepository taxiRepository;
    @Mock
    private MessagingService messagingService;
    @Mock
    private TaxiObserverFactory taxiObserverFactory;
    @Mock
    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        taxiDAO = Mockito.mock(TaxiDAO.class);
        cacheManager = Mockito.mock(Cache.class);
        cachedTaxiService = new CachedTaxiServiceImpl(taxiRepository, messagingService,
                taxiObserverFactory, bookingService);
    }

    @Test
    public void testCreate() {
        StoredTaxi taxi = new StoredTaxi();
        cachedTaxiService.createTaxi(taxi);
        verify(taxiDAO).create(taxi);
        verify(cacheManager).put(taxi.getTaxiNumber(), taxi);
    }

    @Test
    public void testUpdate() {
        StoredTaxi taxi = new StoredTaxi();
        when(taxiDAO.update(taxi)).thenReturn(taxi);
        StoredTaxi result = cachedTaxiService.updateTaxi(taxi);
        assertEquals(taxi, result);
        verify(taxiDAO).update(taxi);
        verify(cacheManager).put(taxi.getTaxiNumber(), taxi);
    }

    @Test
    public void testDelete() {
        StoredTaxi taxi = new StoredTaxi();
        cachedTaxiService.deleteTaxi(taxi.getTaxiNumber());
        verify(taxiDAO).delete(taxi);
    }

    @Test
    public void testFindAll() {
        List<StoredTaxi> taxis = List.of(new StoredTaxi(), new StoredTaxi());
        when(taxiDAO.findAll()).thenReturn(taxis);
        List<StoredTaxi> result = cachedTaxiService.getAllTaxis();
        assertEquals(taxis, result);
        verify(taxiDAO).findAll();
    }
}