package com.eze.backend.restapi.service;

import java.util.List;

public interface IService<T, ID> {

    List<T> getAll();
    T get(ID id);
    void update(T entity);
    void delete(ID id);

}
