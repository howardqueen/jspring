package com.jspring.patterns.behavioral.interpreters;

public interface IExpression<T> {
    void interpret(T context);
}
