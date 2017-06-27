package com.jspring.persistence.sql;

public interface IWhereOr<E extends Enum<?>> extends IOrderable<E> {

	ICondition<IWhereOr<E>> or(E column);

	ICondition<ISubWhere<E, IWhereOr<E>>> orSubWhere(E column);

}
