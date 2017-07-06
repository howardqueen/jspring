package com.jspring.persistence.sql;

public interface IOrderAnd<E extends Enum<?>> extends ILimitable<E> {
	IOrdering<E> and(E column);
}
