package com.jspring.persistence.sql;

public interface IOrderable<E extends Enum<?>> extends ILimitable<E> {
	IOrdering<E> orderBy(E column);
}
