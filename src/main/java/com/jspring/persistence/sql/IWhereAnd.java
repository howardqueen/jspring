package com.jspring.persistence.sql;

public interface IWhereAnd<E extends Enum<?>> extends IOrderable<E> {

	ICondition<IWhereAnd<E>> and(E column);

	ICondition<ISubWhere<E, IWhereAnd<E>>> andSubWhere(E column);

}
