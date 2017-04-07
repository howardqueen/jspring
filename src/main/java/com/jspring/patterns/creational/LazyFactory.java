package com.jspring.patterns.creational;

public abstract class LazyFactory<T> {
	private T t;

	public T get() {
		if (null == t) {
			synchronized (this) {
				if (null == t) {
					t = create();
				}
			}
		}
		return t;
	}

	protected abstract T create();

	public boolean hasCreate() {
		return null != t;
	}
}
