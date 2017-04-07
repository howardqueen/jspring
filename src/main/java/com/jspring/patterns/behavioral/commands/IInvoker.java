package com.jspring.patterns.behavioral.commands;

import java.util.Collection;

public interface IInvoker<T> extends Collection<ICommand<T, ?>> {
}
