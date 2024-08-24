package com.taxifleet.db.dao;

import com.taxifleet.db.StoredDashboard;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

@Singleton
public class DashboardDAO extends BaseDAO<StoredDashboard> {

    @Inject
    public DashboardDAO(SessionFactory sessionFactory) {
        super(StoredDashboard.class, sessionFactory);
    }

    public List<StoredDashboard> getAllDashboards() {
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
            Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
            criteria.select(root);
            return session.createQuery(criteria).getResultList();
        });
    }

    public void saveOrUpdateDashboard(StoredDashboard dashboard) {
        executeInTransaction(session -> {
            session.saveOrUpdate(dashboard);
            return null;
        });
    }

    public StoredDashboard findByBookingId(long bookingId) {
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
            Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
            criteria.select(root).where(builder.equal(root.get("bookingId"), bookingId));
            return session.createQuery(criteria).uniqueResult();
        });
    }

    public List<StoredDashboard> findAllBookingsByTaxiId(String taxiNumber) {
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
            Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
            criteria.select(root).where(builder.equal(root.get("taxiNumber"), taxiNumber));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<StoredDashboard> findByTimeRange(Date startTime, Date endTime) {
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
            Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
            criteria.select(root).where(
                    builder.and(
                            builder.greaterThanOrEqualTo(root.get("startTime"), startTime),
                            builder.lessThanOrEqualTo(root.get("endTime"), endTime)
                    )
            );
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<StoredDashboard> findByLocationRange(double minLatitude,
                                                     double maxLatitude,
                                                     double minLongitude,
                                                     double maxLongitude) {
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
            Root<StoredDashboard> root = criteria.from(StoredDashboard.class);

            Predicate latitudePredicate = builder.between(root.get("bookingLatitude"), minLatitude, maxLatitude);
            Predicate longitudePredicate = builder.between(root.get("bookingLongitude"), minLongitude, maxLongitude);
            criteria.select(root).where(builder.and(latitudePredicate, longitudePredicate));
            return session.createQuery(criteria).getResultList();
        });
    }
}
