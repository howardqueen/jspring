package com.jspring.patterns.behavioral.chains;

import java.util.Collection;

/**
 * Chain of Responsibility
 * @author hqian
 *
 * @param <T>
 * @param <K>
 */
public interface IChain<T, K> extends Collection<IHandler<T, K>> {
    K handle(Object requestor, T context);
}
