package com.taxifleet.db.dao;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.repository.BookingRepository;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class BookingDAO extends BaseDAO<StoredBooking> implements BookingRepository {


    @Inject
    protected BookingDAO(SessionFactory sessionFactory) {
        super(StoredBooking.class, sessionFactory);
    }

    public List<StoredBooking> getAllBookingsForTaxi(String taxiNumber) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
        Root<StoredBooking> root = criteria.from(StoredBooking.class);
        Predicate taxiPredicate = builder.equal(root.get("taxiNumber"), taxiNumber);
        criteria.select(root).where(taxiPredicate);
        return get(criteria);
    }

    @Override
    public StoredBooking createBooking(StoredBooking booking) {
        return save(booking);
    }

    @Override
    public StoredBooking getBooking(long bookingId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
        Root<StoredBooking> root = criteria.from(StoredBooking.class);
        criteria.select(root).where(builder.equal(root.get("bookingId"), bookingId));
        List<StoredBooking> results = get(criteria);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<StoredBooking> getAllBookings() {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
        Root<StoredBooking> root = criteria.from(StoredBooking.class);
        criteria.select(root);
        return get(criteria);
    }

    @Override
    public StoredBooking updateBooking(StoredBooking booking) {
        return update(booking);
    }

    @Override
    public void deleteBooking(Long bookingId) {
        StoredBooking booking = getBooking(bookingId);
        if (booking != null) {
            delete(booking);
        }
    }

    @Override
    public List<StoredBooking> findAllPendingBookings() {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
        Root<StoredBooking> root = criteria.from(StoredBooking.class);
        criteria.select(root).where(builder.equal(root.get("status"), BookingStatus.PENDING));
        return get(criteria);
    }
}