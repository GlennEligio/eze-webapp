package com.eze.backend.restapi.service;

import java.util.List;

public interface IService<T> {

    List<T> getAll();
    T get(String id);
    T create(T entity);
    T update(T entity, String id);
    void delete(String id);

}
