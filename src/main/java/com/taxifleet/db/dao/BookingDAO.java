package com.taxifleet.db.dao;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStatus;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class BookingDAO extends BaseDAO<StoredBooking>{


    @Inject
    protected BookingDAO(SessionFactory sessionFactory) {
        super(StoredBooking.class, sessionFactory);
    }

    public StoredBooking create(StoredBooking storedBooking) {
       return save(storedBooking);
    }

    public void deleteStoredBanking(StoredBooking storedBooking) {
        delete(storedBooking);
    }

    public StoredBooking updateStoredBooking(StoredBooking storedBooking) {
        return update(storedBooking);
    }


    public List<StoredBooking> findAll() {
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
            Root<StoredBooking> root = criteria.from(StoredBooking.class);
            criteria.select(root);
            return session.createQuery(criteria).getResultList();
        });
    }

    public StoredBooking findByBookingId(long bookingId) {
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
            Root<StoredBooking> root = criteria.from(StoredBooking.class);
            criteria.select(root).where(builder.equal(root.get("bookingId"), bookingId));
            return session.createQuery(criteria).uniqueResult();
        });
    }

    public List<StoredBooking> findPendingBookings(){
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
            Root<StoredBooking> root = criteria.from(StoredBooking.class);
            criteria.select(root).where(builder.equal(root.get("status"), BookingStatus.PENDING));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<StoredBooking> getAllBookingsForTaxi(String taxiNumber) {
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
            Root<StoredBooking> root = criteria.from(StoredBooking.class);
            Predicate taxiPredicate = builder.equal(root.get("taxiNumber"), taxiNumber);
            criteria.select(root).where(taxiPredicate);
            return session.createQuery(criteria).getResultList();
        });
    }
}