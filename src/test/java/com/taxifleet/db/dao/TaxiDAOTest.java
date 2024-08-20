//package com.taxifleet.db.dao;
//
//import com.taxifleet.db.StoredTaxi;
//import com.taxifleet.model.Location;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class TaxiDAOTest {
//    private SessionFactory sessionFactory;
//    private Session session;
//    private Transaction transaction;
//    private TaxiDAO taxiDAO;
//
//    @BeforeEach
//    public void setUp() {
//        sessionFactory = Mockito.mock(SessionFactory.class);
//        session = Mockito.mock(Session.class);
//        transaction = Mockito.mock(Transaction.class);
//        taxiDAO = new TaxiDAO(sessionFactory);
//
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.beginTransaction()).thenReturn(transaction);
//    }
//
//    @Test
//    public void testCreate() {
//        StoredTaxi taxi = new StoredTaxi();
//        taxiDAO.create(taxi);
//        verify(session).save(taxi);
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testUpdate() {
//        StoredTaxi taxi = new StoredTaxi();
//        when(session.merge(taxi)).thenReturn(taxi);
//        StoredTaxi result = taxiDAO.update(taxi);
//        assertEquals(taxi, result);
//        verify(session).merge(taxi);
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testDelete() {
//        StoredTaxi taxi = new StoredTaxi();
//        taxiDAO.delete(taxi);
//        verify(session).delete(taxi);
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testFindByTaxiNumber() {
//        CriteriaBuilder builder = Mockito.mock(CriteriaBuilder.class);
//        CriteriaQuery<StoredTaxi> criteria = Mockito.mock(CriteriaQuery.class);
//        Root<StoredTaxi> root = Mockito.mock(Root.class);
//
//        when(session.getCriteriaBuilder()).thenReturn(builder);
//        when(builder.createQuery(StoredTaxi.class)).thenReturn(criteria);
//        when(criteria.from(StoredTaxi.class)).thenReturn(root);
//        when(criteria.select(root)).thenReturn(criteria);
//        when(criteria.where(builder.equal(root.get("taxiNumber"), "TX123"))).thenReturn(criteria);
//        when(session.createQuery(criteria).uniqueResult()).thenReturn(new StoredTaxi());
//
//        StoredTaxi result = taxiDAO.findByTaxiNumber("TX123");
//        assertNotNull(result);
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testUpdateLocation() {
//        StoredTaxi taxi = new StoredTaxi();
//        taxi.setTaxiNumber("TX123");
//        Location location = new Location(12.34, 56.78);
//
//        when(session.getCriteriaBuilder()).thenReturn(Mockito.mock(CriteriaBuilder.class));
//        when(session.createQuery(any(CriteriaQuery.class)).uniqueResult()).thenReturn(taxi);
//        when(session.merge(taxi)).thenReturn(taxi);
//
//        taxiDAO.updateLocation("TX123", location);
//        assertEquals(12.34, taxi.getLatitude());
//        assertEquals(56.78, taxi.getLongitude());
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testFindAllWithStatusAndLocation() {
//        CriteriaBuilder builder = Mockito.mock(CriteriaBuilder.class);
//        CriteriaQuery<StoredTaxi> criteria = Mockito.mock(CriteriaQuery.class);
//        Root<StoredTaxi> root = Mockito.mock(Root.class);
//
//        when(session.getCriteriaBuilder()).thenReturn(builder);
//        when(builder.createQuery(StoredTaxi.class)).thenReturn(criteria);
//        when(criteria.from(StoredTaxi.class)).thenReturn(root);
//        when(session.createQuery(criteria).getResultList()).thenReturn(List.of(new StoredTaxi()));
//
//        List<StoredTaxi> result = taxiDAO.findAllWithStatusAndLocation();
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testFindNearbyTaxis() {
//        CriteriaBuilder builder = Mockito.mock(CriteriaBuilder.class);
//        CriteriaQuery<StoredTaxi> criteria = Mockito.mock(CriteriaQuery.class);
//        Root<StoredTaxi> root = Mockito.mock(Root.class);
//
//        when(session.getCriteriaBuilder()).thenReturn(builder);
//        when(builder.createQuery(StoredTaxi.class)).thenReturn(criteria);
//        when(criteria.from(StoredTaxi.class)).thenReturn(root);
//        when(session.createQuery(criteria).getResultList()).thenReturn(List.of(new StoredTaxi()));
//
//        List<StoredTaxi> result = taxiDAO.findNearbyTaxis(12.34, 56.78, 5.0);
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(transaction).commit();
//    }
//}