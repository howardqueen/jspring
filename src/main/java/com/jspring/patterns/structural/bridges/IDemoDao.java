package com.jspring.patterns.structural.bridges;

import java.util.List;

public interface IDemoDao {
    List<Object> select();

    Object select(Object key);

    int insert(Object o);

    int update(Object o);

    int delete(Object o);
}
