package com.jspring.persistence.sql;

public interface IGroupable<E extends Enum<?>> extends IOrderable<E> {

	void groupBy(E[] columns);

}
