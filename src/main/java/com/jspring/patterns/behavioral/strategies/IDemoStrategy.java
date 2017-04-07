package com.jspring.patterns.behavioral.strategies;

public interface IDemoStrategy<T, K> {
    T getArgs();

    K execute();
}
