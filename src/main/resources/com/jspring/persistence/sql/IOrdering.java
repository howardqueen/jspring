package com.jspring.persistence.sql;

public interface IOrdering<E extends Enum<?>> {
	
	IOrderAnd<E> ascending();

	IOrderAnd<E> descending();
	
}
