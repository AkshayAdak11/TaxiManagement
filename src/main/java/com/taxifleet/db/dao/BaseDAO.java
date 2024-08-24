package com.taxifleet.db.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class BaseDAO<T> {

    protected final Class<T> dtoClass;
    protected final SessionFactory sessionFactory;

    protected BaseDAO(Class<T> dtoClass, SessionFactory sessionFactory) {
        this.dtoClass = dtoClass;
        this.sessionFactory = sessionFactory;
    }

    protected Session openSession() {
        return sessionFactory.openSession();
    }

    protected <R> R executeInTransaction(SessionAction<R> action) {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        try {
            R result = action.execute(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public T update(T dto) {
        return executeInTransaction(session -> {
            session.update(dto);
            return dto;
        });
    }

    public T save(T dto) {
        return executeInTransaction(session -> {
            session.save(dto);
            return dto;
        });
    }

    public T delete(T dto) {
        return executeInTransaction(session -> {
            session.delete(dto);
            return dto;
        });
    }

    @FunctionalInterface
    protected interface SessionAction<R> {
        R execute(Session session);
    }
}

