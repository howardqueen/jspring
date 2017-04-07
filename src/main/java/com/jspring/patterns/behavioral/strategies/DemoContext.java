package com.jspring.patterns.behavioral.strategies;

public class DemoContext<T, K> {
    public K execute(IDemoStrategy<T, K> strategy) {
        return strategy.execute();
    }
}
