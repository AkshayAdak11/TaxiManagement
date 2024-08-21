package com.taxifleet.db.dao;

import com.taxifleet.db.StoredBooking;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.model.Location;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class BookingDAO {

    private final SessionFactory sessionFactory;

    @Inject
    public BookingDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<StoredBooking> findAll() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
            Root<StoredBooking> root = criteria.from(StoredBooking.class);
            criteria.select(root);
            List<StoredBooking> result = session.createQuery(criteria).getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public StoredBooking create(StoredBooking storedBooking) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(storedBooking);
            transaction.commit();
            return storedBooking;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void delete(StoredBooking storedBooking) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.delete(storedBooking);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public StoredBooking update(StoredBooking storedBooking) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(storedBooking);
            transaction.commit();
            return storedBooking;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<StoredBooking> findbookinsBylocation(Location location) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
            Root<StoredBooking> root = criteria.from(StoredBooking.class);
            criteria.select(root);
            List<StoredBooking> result = session.createQuery(criteria).getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public StoredBooking findByBookingId(long bookingId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
            Root<StoredBooking> root = criteria.from(StoredBooking.class);
            criteria.select(root).where(builder.equal(root.get("bookingId"), bookingId));
            StoredBooking result = session.createQuery(criteria).uniqueResult();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }


    public List<StoredBooking> findPendingBookings() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
            Root<StoredBooking> root = criteria.from(StoredBooking.class);
            criteria.select(root).where(builder.equal(root.get("status"), BookingStatus.PENDING));
            List<StoredBooking> result = session.createQuery(criteria).getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<StoredBooking> getAllBookingsForTaxi(String taxiNumber) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredBooking> criteria = builder.createQuery(StoredBooking.class);
            Root<StoredBooking> root = criteria.from(StoredBooking.class);
            criteria.select(root).where(builder.equal(root.get("taxiNumber"), taxiNumber));
            List<StoredBooking> result = session.createQuery(criteria).getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}