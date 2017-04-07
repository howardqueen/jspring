package com.jspring.patterns.behavioral.commands;

import org.slf4j.*;

public abstract class Command<T, K> implements ICommand<T, K> {

    protected final Logger log;
    private final T receiver;
    private final K context;

    public Command(T receiver, K context) {
        log = LoggerFactory.getLogger(this.getClass());
        this.receiver = receiver;
        this.context = context;
    }

    @Override
    public T getReceiver() {
        return receiver;
    }

    @Override
    public K getContext() {
        return context;
    }

}
