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

//
//    public StoredTaxi findById(String taxiNumber) {
//        Session session = sessionFactory.openSession();
//        Transaction transaction = session.beginTransaction();
//        try {
//            StoredTaxi result = session.get(StoredTaxi.class, taxiNumber);
//            transaction.commit();
//            return result;
//        } catch (Exception e) {
//            transaction.rollback();
//            throw e;
//        } finally {
//            session.close();
//        }
//    }

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


    public void update(StoredTaxi taxi) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(taxi);
            transaction.commit();
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

    public void updateLocation(String taxiNumber, Location location) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            StoredTaxi taxi = findByTaxiNumber(taxiNumber);
            if (taxi != null) {
                taxi.setFromLatitude(location.getLatitude());
                taxi.setFromLongitude(location.getLongitude());
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


    public StoredTaxi findByTaxiNumber(String taxiNumber) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<StoredTaxi> criteria = builder.createQuery(StoredTaxi.class);
            Root<StoredTaxi> root = criteria.from(StoredTaxi.class);
            criteria.select(root).where(builder.equal(root.get("taxiNumber"), taxiNumber));
            StoredTaxi result = session.createQuery(criteria).uniqueResult();
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