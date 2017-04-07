package com.jspring.patterns.behavioral.iterators;

import java.util.Iterator;

public class DemoIterator<T> implements Iterator<T> {

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        return null;
    }

    @Override
    public void remove() {
    }
}
