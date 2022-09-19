package com.edu.ulab.app.storage;

import java.util.Optional;

public interface Store<T> {
    T create(T entity);

    T update(T entity);

    Optional<T> getById(Long id);

    void delete(T entity);
}
