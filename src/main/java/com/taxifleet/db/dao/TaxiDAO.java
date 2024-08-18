package com.taxifleet.db.dao;

import com.taxifleet.db.StoredTaxi;
import com.taxifleet.model.Location;
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
public class TaxiDAO {

    private final SessionFactory sessionFactory;

    @Inject
    public TaxiDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<StoredTaxi> findAll() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredTaxi> criteria = builder.createQuery(StoredTaxi.class);
            Root<StoredTaxi> root = criteria.from(StoredTaxi.class);
            criteria.select(root);
            List<StoredTaxi> result = session.createQuery(criteria).getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public StoredTaxi findById(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            StoredTaxi result = session.get(StoredTaxi.class, id);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public StoredTaxi create(StoredTaxi taxi) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(taxi);
            transaction.commit();
            return taxi;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public StoredTaxi update(StoredTaxi taxi) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            StoredTaxi result = (StoredTaxi) session.merge(taxi);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void delete(StoredTaxi taxi) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.delete(taxi);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void updateLocation(Long id, Location location) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            StoredTaxi taxi = findById(id);
            if (taxi != null) {
                taxi.setLatitude(location.getLatitude());
                taxi.setLongitude(location.getLongitude());
                update(taxi);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<StoredTaxi> findAllWithStatusAndLocation() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredTaxi> criteria = builder.createQuery(StoredTaxi.class);
            Root<StoredTaxi> root = criteria.from(StoredTaxi.class);
            criteria.select(root);
            List<StoredTaxi> result = session.createQuery(criteria).getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredTaxi> criteria = builder.createQuery(StoredTaxi.class);
            Root<StoredTaxi> root = criteria.from(StoredTaxi.class);
            criteria.select(root).where(
                    builder.and(
                            builder.between(root.get("location").get("latitude"), latitude - radius, latitude + radius),
                            builder.between(root.get("location").get("longitude"), longitude - radius, longitude + radius)
                    )
            );
            List<StoredTaxi> result = session.createQuery(criteria).getResultList();
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