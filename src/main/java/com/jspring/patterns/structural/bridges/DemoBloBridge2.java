package com.jspring.patterns.structural.bridges;

import java.util.List;

public class DemoBloBridge2 implements IDemoBlo {

    private final IDemoDao dao;

    public DemoBloBridge2(IDemoDao dao) {
        this.dao = dao;
    }

    @Override
    public IDemoDao getDao() {
        return dao;
    }

    @Override
    public List<Object> getList() {
        return dao.select();
    }

    @Override
    public Object get(Object key) {
        return dao.select(key);
    }

    @Override
    public boolean add(Object o) {
        return dao.insert(o) > 0;
    }

    @Override
    public boolean update(Object o) {
        return dao.update(o) > 0;
    }

    @Override
    public boolean remove(List<Object> list) {
        for (Object o : list) {
            //It's different with BloBridge1 here.
            if (dao.delete(o) < 0) { return false; }
        }
        return true;
    }

    @Override
    public void act1() {
        System.out.println("Do anything with dao here.");
    }

}
