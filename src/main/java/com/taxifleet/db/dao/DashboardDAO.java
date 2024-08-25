package com.taxifleet.db.dao;

import com.taxifleet.db.StoredDashboard;
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
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
        Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
        criteria.select(root);
        return get(criteria);
    }

    public void saveOrUpdateDashboard(StoredDashboard dashboard) {
        executeInTransaction(session -> {
            session.saveOrUpdate(dashboard);
            return null;
        });
    }

    public StoredDashboard findByBookingId(long bookingId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
        Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
        criteria.select(root).where(builder.equal(root.get("bookingId"), bookingId));
        List<StoredDashboard> results = get(criteria);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<StoredDashboard> findAllBookingsByTaxiId(String taxiNumber) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
        Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
        criteria.select(root).where(builder.equal(root.get("taxiNumber"), taxiNumber));
        return get(criteria);
    }

    public List<StoredDashboard> findByTimeRange(Date startTime, Date endTime) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
        Root<StoredDashboard> root = criteria.from(StoredDashboard.class);
        criteria.select(root).where(
                builder.and(
                        builder.greaterThanOrEqualTo(root.get("startTime"), startTime),
                        builder.lessThanOrEqualTo(root.get("endTime"), endTime)
                )
        );
        return get(criteria);
    }


    public List<StoredDashboard> findByLocationRange(double minLatitude,
                                                     double maxLatitude,
                                                     double minLongitude,
                                                     double maxLongitude) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredDashboard> criteria = builder.createQuery(StoredDashboard.class);
        Root<StoredDashboard> root = criteria.from(StoredDashboard.class);

        Predicate latitudePredicate = builder.between(root.get("bookingLatitude"), minLatitude, maxLatitude);
        Predicate longitudePredicate = builder.between(root.get("bookingLongitude"), minLongitude, maxLongitude);
        criteria.select(root).where(builder.and(latitudePredicate, longitudePredicate));
        return get(criteria);
    }
}
