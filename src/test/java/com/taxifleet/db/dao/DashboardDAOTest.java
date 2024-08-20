//package com.taxifleet.db.dao;
//
//import com.taxifleet.db.StoredDashboard;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class DashboardDAOTest {
//    private SessionFactory sessionFactory;
//    private Session session;
//    private Transaction transaction;
//    private DashboardDAO dashboardDAO;
//
//    @BeforeEach
//    public void setUp() {
//        sessionFactory = Mockito.mock(SessionFactory.class);
//        session = Mockito.mock(Session.class);
//        transaction = Mockito.mock(Transaction.class);
//        dashboardDAO = new DashboardDAO(sessionFactory);
//
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.beginTransaction()).thenReturn(transaction);
//    }
//
//    @Test
//    public void testGetAllDashboards() {
//        StoredDashboard dashboard = new StoredDashboard();
//        when(session.createQuery("from StoredDashboard", StoredDashboard.class).list()).thenReturn(List.of(dashboard));
//
//        StoredDashboard result = dashboardDAO.getAllDashboards();
//        assertNotNull(result);
//        assertEquals(dashboard, result);
//        verify(transaction).commit();
//    }
//
//    @Test
//    public void testSaveOrUpdateDashboard() {
//        StoredDashboard dashboard = new StoredDashboard();
//        dashboardDAO.saveOrUpdateDashboard(dashboard);
//        verify(session).saveOrUpdate(dashboard);
//        verify(transaction).commit();
//    }
//}