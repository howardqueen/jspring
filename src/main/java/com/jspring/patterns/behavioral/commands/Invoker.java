package com.jspring.patterns.behavioral.commands;

import org.slf4j.*;

import java.util.ArrayList;

public abstract class Invoker<T> extends ArrayList<ICommand<T, ?>> implements
        IInvoker<T> {
    private static final long serialVersionUID = 1L;

    protected final Logger log;
    protected final T receiver;

    public Invoker(T receiver) {
        log = LoggerFactory.getLogger(this.getClass());
        this.receiver = receiver;
    }
}
