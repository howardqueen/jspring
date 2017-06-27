package com.jspring.persistence.sql;

public interface IUpdateAnd<E extends Enum<?>> {

	IUpdateAnd<E> and(E column, Object value);

	ICondition<IUpdateWhereable<E>> where(E column, Object value);

}
