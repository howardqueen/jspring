package com.jspring.patterns.structural.bridges;

import java.util.List;

public interface IDemoBlo {
    IDemoDao getDao();

    List<Object> getList();

    Object get(Object key);

    boolean add(Object o);

    boolean update(Object o);

    boolean remove(List<Object> list);

    void act1();
}
