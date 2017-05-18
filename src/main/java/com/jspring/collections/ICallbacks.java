package com.jspring.collections;

public interface ICallbacks<T, K> {
    K callback(T result);
}
