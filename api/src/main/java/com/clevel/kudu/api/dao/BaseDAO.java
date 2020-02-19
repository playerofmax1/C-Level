package com.clevel.kudu.api.dao;


import com.clevel.kudu.api.exception.RecordNotFoundException;

import java.io.Serializable;
import java.util.List;

public interface BaseDAO<T,ID extends Serializable> {
    T findById(ID id) throws RecordNotFoundException;
    List<T> findAll();
    T persist(T entity);
    T merge(T entity);
    void remove(T entity);
}
