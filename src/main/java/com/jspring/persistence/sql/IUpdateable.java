package com.jspring.persistence.sql;

public interface IUpdateable<T, E extends Enum<?>> {

	IUpdateAnd<E> update(E column, Object value);

	ISqlUpdateBuilder update(T entity);

	// IStreamUpdateBuilder insert(T[] entities);
	//
	// IStreamUpdateBuilder replace(T[] entities);

}
