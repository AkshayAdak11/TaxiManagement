package com.taxifleet.db.dao;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredDashboard;
import com.taxifleet.db.StoredTaxi;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
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

            // Assuming there is only one record, return the first (and only) element
            if (dashboards != null && !dashboards.isEmpty()) {
                return dashboards;
            } else {
                return null; // or throw an exception if that's your preferred handling
            }
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

    public StoredDashboard findByBookingId(long bookingId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
            Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
            criteria.select(root).where(
                    builder.and(
                            builder.equal(root.get("bookingId"), bookingId)
                    )
            );
            StoredDashboard result = session.createQuery(criteria).uniqueResult();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }


    public List<StoredDashboard> findAllBookingsByTaxiId(String taxiNumber) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
            Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
            criteria.select(root).where(builder.equal(root.get("taxiNumber"), taxiNumber));
            List<StoredDashboard> result = session.createQuery(criteria).getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<StoredDashboard> findByTimeRange(Date startTime, Date endTime) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
            Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
            criteria.select(root)
                    .where(
                            builder.and(
                                    builder.greaterThanOrEqualTo(root.get("startTime"), startTime),
                                    builder.lessThanOrEqualTo(root.get("endTime"), endTime)
                            )
                    );

            List<StoredDashboard> results = session.createQuery(criteria).getResultList();
            transaction.commit();
            return results;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }


    public List<StoredDashboard> findByLocationRange(double bookingLatitude, double bookingLongitude) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
            Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
            criteria.select(root)
                    .where(
                            builder.and(
                                    builder.greaterThanOrEqualTo(root.get("bookingLatitude"), bookingLatitude),
                                    builder.lessThanOrEqualTo(root.get("bookingLongitude"), bookingLongitude)
                            )
                    );
            List<StoredDashboard> results = session.createQuery(criteria).getResultList();
            transaction.commit();
            return results;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}