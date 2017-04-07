package com.jspring.patterns.behavioral.chains;

/**
 * Responsor
 * @author HowardQian(howard.queen@qq.com), 2012-05-08 12:53
 */
public interface IHandler<T, K> {
    K handle(Object requestor, T context);
}
