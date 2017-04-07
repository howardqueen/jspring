package com.jspring.patterns.behavioral.commands;

public interface ICommand<T, K> {
    T getReceiver();

    K getContext();

    void execute();

    void rollback();
}
