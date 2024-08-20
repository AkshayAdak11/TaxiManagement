//package com.taxifleet.db.dao;
//
//import com.taxifleet.db.StoredBooking;
//import com.taxifleet.enums.BookingStatus;
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
//public class BookingDAOTest {
//    private SessionFactory sessionFactory;
//    private Session session;
//    private Transaction transaction;
//    private BookingDAO bookingDAO;
//
//    @BeforeEach
//    public void setUp() {
//        sessionFactory = Mockito.mock(SessionFactory.class);
//        session = Mockito.mock(Session.class);
//        transaction = Mockito.mock(Transaction.class);
//        bookingDAO = new BookingDAO(sessionFactory);
//
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.beginTransaction()).thenReturn(transaction);
//    }
//
//    @Test
//    public void testFindAll() {
//        CriteriaBuilder builder = Mockito.mock(CriteriaBuilder.class);
//        CriteriaQuery<StoredBooking> criteria = Mockito.mock(CriteriaQuery.class);
//        Root<StoredBooking> root = Mockito.mock(Root.class);
//
//        when(session.getCriteriaBuilder()).thenReturn(builder);
//        when(builder.createQuery(StoredBooking.class)).thenReturn(criteria);
//        when(criteria.from(StoredBooking.class)).thenReturn(root);
//        when(session.createQuery(criteria).getResultList()).thenReturn(List.of(new StoredBooking()));
//
//        List<StoredBooking> result = bookingDAO.findAll();
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testCreate() {
//        StoredBooking booking = new StoredBooking();
//        bookingDAO.create(booking);
//        verify(session).save(booking);
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testDelete() {
//        StoredBooking booking = new StoredBooking();
//        bookingDAO.delete(booking);
//        verify(session).delete(booking);
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testUpdate() {
//        StoredBooking booking = new StoredBooking();
//        bookingDAO.update(booking);
//        verify(session).update(booking);
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testFindByBookingId() {
//        CriteriaBuilder builder = Mockito.mock(CriteriaBuilder.class);
//        CriteriaQuery<StoredBooking> criteria = Mockito.mock(CriteriaQuery.class);
//        Root<StoredBooking> root = Mockito.mock(Root.class);
//
//        when(session.getCriteriaBuilder()).thenReturn(builder);
//        when(builder.createQuery(StoredBooking.class)).thenReturn(criteria);
//        when(criteria.from(StoredBooking.class)).thenReturn(root);
//        when(session.createQuery(criteria).uniqueResult()).thenReturn(new StoredBooking());
//
//        StoredBooking result = bookingDAO.findByBookingId(1L);
//        assertNotNull(result);
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testFindPendingBookings() {
//        CriteriaBuilder builder = Mockito.mock(CriteriaBuilder.class);
//        CriteriaQuery<StoredBooking> criteria = Mockito.mock(CriteriaQuery.class);
//        Root<StoredBooking> root = Mockito.mock(Root.class);
//
//        when(session.getCriteriaBuilder()).thenReturn(builder);
//        when(builder.createQuery(StoredBooking.class)).thenReturn(criteria);
//        when(criteria.from(StoredBooking.class)).thenReturn(root);
//        when(session.createQuery(criteria).getResultList()).thenReturn(List.of(new StoredBooking()));
//
//        List<StoredBooking> result = bookingDAO.findPendingBookings();
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(transaction).commit();
//    }
//}