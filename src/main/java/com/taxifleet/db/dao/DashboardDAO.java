package com.taxifleet.db.dao;

import com.taxifleet.db.StoredDashboard;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class DashboardDAO {

    private final SessionFactory sessionFactory;

    @Inject
    public DashboardDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<StoredDashboard> getAllDashboards() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<StoredDashboard> dashboards = session.createQuery("from StoredDashboard", StoredDashboard.class).list();
            transaction.commit();
            return dashboards;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void saveOrUpdateDashboard(StoredDashboard dashboard) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.saveOrUpdate(dashboard);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}