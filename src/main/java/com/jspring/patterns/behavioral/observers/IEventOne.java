package com.jspring.patterns.behavioral.observers;

public interface IEventOne<T> {

	public void fire(Object sender, T context);

	public void tryFire(Object sender, T context);

	public void setListener(IListener<T> listener);

}
