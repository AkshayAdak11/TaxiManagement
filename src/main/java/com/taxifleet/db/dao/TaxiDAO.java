package com.taxifleet.db.dao;

import com.taxifleet.db.StoredTaxi;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class TaxiDAO extends BaseDAO<StoredTaxi> {

    @Inject
    public TaxiDAO(SessionFactory sessionFactory) {
        super(StoredTaxi.class, sessionFactory);
    }

    public StoredTaxi create(StoredTaxi taxi) {
        return save(taxi);
    }

    public void deleteTaxi(StoredTaxi taxi) {
        delete(taxi);
    }

    public void updateTaxi(StoredTaxi taxi) {
        update(taxi);
    }

    public List<StoredTaxi> getAllTaxis() {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredTaxi> criteria = builder.createQuery(StoredTaxi.class);
        Root<StoredTaxi> root = criteria.from(StoredTaxi.class);
        criteria.select(root);
        return get(criteria);
    }


    public StoredTaxi findByTaxiNumber(String taxiNumber) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredTaxi> criteria = builder.createQuery(StoredTaxi.class);
        Root<StoredTaxi> root = criteria.from(StoredTaxi.class);
        criteria.select(root).where(builder.equal(root.get("taxiNumber"), taxiNumber));
        List<StoredTaxi> results = get(criteria);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<StoredTaxi> findNearbyTaxis(Double latitude, Double longitude, Double radius) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<StoredTaxi> criteria = builder.createQuery(StoredTaxi.class);
        Root<StoredTaxi> root = criteria.from(StoredTaxi.class);
        criteria.select(root).where(
                builder.and(
                        builder.between(root.get("location").get("latitude"), latitude - radius, latitude + radius),
                        builder.between(root.get("location").get("longitude"), longitude - radius, longitude + radius)
                )
        );
        return get(criteria);
    }
}
