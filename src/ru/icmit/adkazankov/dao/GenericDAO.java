package ru.icmit.adkazankov.dao;


import ru.icmit.adkazankov.domain.Entity;

import java.util.List;

public interface GenericDAO<T extends Entity> {
    T create(T o);
    void update(T o);
    void delete(T o);
    List<T> getAll();
    T getByKey(Long id);
}
