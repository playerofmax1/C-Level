package com.clevel.kudu.api.dao;

import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.system.Application;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class GenericDAO<T, ID extends Serializable> implements BaseDAO<T, ID>, Serializable {
    @Inject
    private Logger log;

    @Inject
    protected Application app;
    protected Class<T> clazz;
    protected CriteriaBuilder cb;
    protected Root<T> root;
    protected EntityGraph<T> graph;
    protected static final String FETCH_GRAPH = "javax.persistence.fetchgraph";
    @PersistenceContext
    protected EntityManager em;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void onCreation() {
        this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.graph = em.createEntityGraph(clazz);
    }

    @SuppressWarnings("unchecked")
    public T findById(ID id) throws RecordNotFoundException {
        CriteriaQuery<T> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get("id"), id)
        );

        Query query = em.createQuery(criteria);

        List<T> list = query.getResultList();
        if (list.isEmpty()) {
            log.error("record not found! (id: {})",id);
            throw new RecordNotFoundException("record not found!");
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<T> findAll() {
        TypedQuery<T> query = createTypeQuery();
        return query.getResultList();
    }

    @Override
    public T persist(T entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public T merge(T entity) {
        em.merge(entity);
        return entity;
    }

    public void persist(List<T> entities) {
        entities.forEach(this::persist);
    }

    public void merge(List<T> entities) {
        entities.forEach(this::merge);
    }

    @Override
    public void remove(T entity) {
        em.remove(entity);
    }

    public void remove(List<T> entities) {
        entities.forEach(this::remove);
    }

    public CriteriaQuery<T> createCriteriaQuery() {
        cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        root = criteriaQuery.from(clazz);
        return criteriaQuery;
    }

    protected CriteriaQuery createCriteriaCount() {
        cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        root = criteriaQuery.from(clazz);
        return criteriaQuery;
    }

    protected CriteriaUpdate<T> createCriteriaUpdate() {
        cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = cb.createCriteriaUpdate(clazz);
        root = criteriaUpdate.from(clazz);
        return criteriaUpdate;
    }

    protected CriteriaDelete<T> createCriteriaDelete() {
        cb = em.getCriteriaBuilder();
        CriteriaDelete<T> criteriaDelete = cb.createCriteriaDelete(clazz);
        root = criteriaDelete.from(clazz);
        return criteriaDelete;
    }

    public Query createNativeQuery(String query) {
        return em.createNativeQuery(query);
    }

    private TypedQuery<T> createTypeQuery() {
        CriteriaQuery<T> criteria = createCriteriaQuery();
        return em.createQuery(criteria.select(root));
    }

}