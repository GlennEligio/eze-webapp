package com.eze.backend.restapi.service;

import java.io.Serializable;
import java.util.List;

public interface IService<T> {

    List<T> getAll();
    T get(Serializable id);
    T create(T entity);
    T update(T entity, Serializable id);
    void delete(Serializable id);
    String notFound(Serializable id);
    String alreadyExist(Serializable id);
}
