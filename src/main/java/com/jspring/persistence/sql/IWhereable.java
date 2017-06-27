package com.jspring.persistence.sql;

public interface IWhereable<E extends Enum<?>> extends IGroupable<E> {

	ICondition<IWhere<E>> where(E column);
}
