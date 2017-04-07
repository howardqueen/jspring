package com.jspring.patterns.behavioral.mementos;

public interface IOriginator<T> {
    T storeToMementor();

    void restoreFromMementor(T mementor);
}
