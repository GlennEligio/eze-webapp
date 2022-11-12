package com.eze.backend.spring.service;

import java.io.Serializable;
import java.util.List;

public interface IService<T> {

    List<T> getAll();
    List<T> getAllNotDeleted();
    T get(Serializable id);
    T create(T entity);
    T update(T entity, Serializable id);
    void delete(Serializable id);
    void softDelete(Serializable id);
    String notFound(Serializable id);
    String alreadyExist(Serializable id);
    int addOrUpdate(List<T> entities, boolean overwrite);
}
