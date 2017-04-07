package com.jspring.patterns.behavioral.observers;

import java.util.Collection;

public interface IEvent<T> extends Collection<IListener<T>> {
	
    public void fire(Object sender, T context);

    public void tryFire(Object sender, T context);
    
}
