package com.jspring.persistence.sql;

public interface ISubWhereOr<E extends Enum<?>, HOLDER> {

	ICondition<ISubWhereOr<E, HOLDER>> or(E column);

	// Condition<WheresEnd<E>> orWheresAnd(E column);
	//
	// Condition<WheresEnd<E>> orWheresOr(E column);

	HOLDER endSubWhere();
}
