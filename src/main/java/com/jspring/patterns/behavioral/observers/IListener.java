package com.jspring.patterns.behavioral.observers;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-04-26 14:56
 */
public interface IListener<T> {
    void eventOccur(Object sender, T context);
}
