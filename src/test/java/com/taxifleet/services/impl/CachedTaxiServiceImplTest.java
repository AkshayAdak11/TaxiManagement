//package com.taxifleet.services.impl;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import com.taxifleet.db.StoredTaxi;
//import com.taxifleet.enums.TaxiStatus;
//import com.taxifleet.repository.TaxiRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static org.mockito.Mockito.*;
//
//class CachedTaxiServiceImplTest {
//
//    @Mock
//    private TaxiRepository taxiRepository;
//
//    @InjectMocks
//    private CachedTaxiServiceImpl cachedTaxiService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        Cache<Long, StoredTaxi> taxiCache = Caffeine.newBuilder()
//                .expireAfterWrite(10, TimeUnit.MINUTES)
//                .maximumSize(100)
//                .build();
//        cachedTaxiService = new CachedTaxiServiceImpl(taxiRepository);
//    }
//
//    @Test
//    void testGetAllTaxis() {
//        List<StoredTaxi> taxis = Arrays.asList(new StoredTaxi(), new StoredTaxi());
//        when(taxiRepository.getAllTaxis()).thenReturn(taxis);
//
//        List<StoredTaxi> result = cachedTaxiService.getAllTaxis();
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//    }
//
//    @Test
//    void testGetTaxi() {
//        StoredTaxi taxi = new StoredTaxi();
//        when(taxiRepository.getTaxi(1L)).thenReturn(taxi);
//
//        StoredTaxi result = cachedTaxiService.getTaxi(1L);
//
//        assertEquals(taxi, result);
//        verify(taxiRepository, times(1)).getTaxi(1L);
//    }
//
//    @Test
//    void testCreateTaxi() {
//        StoredTaxi newTaxi = new StoredTaxi();
//        newTaxi.setId(1L);
//        when(taxiRepository.createTaxi(any(StoredTaxi.class))).thenReturn(newTaxi);
//
//        StoredTaxi result = cachedTaxiService.createTaxi(newTaxi);
//
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    void testBookTaxi() {
//        StoredTaxi updatedTaxi = new StoredTaxi();
//        updatedTaxi.setId(1L);
//        when(taxiRepository.updateTaxi(any(StoredTaxi.class))).thenReturn(updatedTaxi);
//
//        StoredTaxi result = cachedTaxiService.bookTaxi(updatedTaxi, 1L);
//
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    void testUpdateTaxi() {
//        StoredTaxi updatedTaxi = new StoredTaxi();
//        updatedTaxi.setId(1L);
//        when(taxiRepository.updateTaxi(any(StoredTaxi.class))).thenReturn(updatedTaxi);
//
//        StoredTaxi result = cachedTaxiService.updateTaxi(updatedTaxi);
//
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    void testDeleteTaxi() {
//        cachedTaxiService.deleteTaxi(1L);
//        verify(taxiRepository, times(1)).deleteTaxi(1L);
//    }
//
//    @Test
//    void testSetTaxiAvailability() {
//        StoredTaxi taxi = new StoredTaxi();
//        when(taxiRepository.getTaxi(1L)).thenReturn(taxi);
//
//        cachedTaxiService.setTaxiAvailability(1L, true);
//
//        assertTrue(taxi.isAvailable());
//        verify(taxiRepository, times(1)).updateTaxi(taxi);
//    }
//
//    @Test
//    void testFindNearbyTaxis() {
//        List<StoredTaxi> taxis = Collections.singletonList(new StoredTaxi());
//        when(taxiRepository.findNearbyTaxis(1.0, 1.0, 10.0)).thenReturn(taxis);
//
//        List<StoredTaxi> result = cachedTaxiService.findNearbyTaxis(1.0, 1.0, 10.0);
//
//        assertEquals(taxis, result);
//        verify(taxiRepository, times(1)).findNearbyTaxis(1.0, 1.0, 10.0);
//    }
//
//    @Test
//    void testFindNearByAvailableTaxi() {
//        StoredTaxi taxi = new StoredTaxi();
//        taxi.setAvailable(true);
//        taxi.setStatus(TaxiStatus.AVAILABLE);
//        when(taxiRepository.findNearbyTaxis(1.0, 1.0, 10.0)).thenReturn(Collections.singletonList(taxi));
//
//        StoredTaxi result = cachedTaxiService.findNearByAvailableTaxi(1.0, 1.0, 10.0);
//
//        assertEquals(taxi, result);
//        verify(taxiRepository, times(1)).findNearbyTaxis(1.0, 1.0, 10.0);
//    }
//
//    @Test
//    void testUpdateTaxiStatus() {
//        StoredTaxi taxi = new StoredTaxi();
//        cachedTaxiService.updateTaxiStatus(taxi, TaxiStatus.BOOKED);
//        assertEquals(TaxiStatus.BOOKED, taxi.getStatus());
//        verify(taxiRepository, times(1)).updateTaxi(taxi);
//    }
//
//    @Test
//    void testIsTaxiAvailable() {
//        StoredTaxi taxi = new StoredTaxi();
//        taxi.setAvailable(true);
//        taxi.setStatus(TaxiStatus.AVAILABLE);
//        when(taxiRepository.getAllTaxis()).thenReturn(Collections.singletonList(taxi));
//
//        boolean result = cachedTaxiService.isTaxiAvailable();
//
//        assertTrue(result);
//        verify(taxiRepository, times(1)).getAllTaxis();
//    }
//
//    @Test
//    void testGetTaxiAvailable() {
//        StoredTaxi taxi = new StoredTaxi();
//        taxi.setAvailable(true);
//        taxi.setStatus(TaxiStatus.AVAILABLE);
//        when(taxiRepository.getAllTaxis()).thenReturn(Collections.singletonList(taxi));
//
//        StoredTaxi result = cachedTaxiService.getTaxiAvailable();
//
//        assertEquals(taxi, result);
//        verify(taxiRepository, times(1)).getAllTaxis();
//    }
//}