package com.jspring.persistence.sql;

public interface ISubWhereAnd<E extends Enum<?>, HOLDER> {

	ICondition<ISubWhereAnd<E, HOLDER>> and(E column);

	// Condition<IWheresAnd<E>, IWheresAnd<E>> andWheresAnd(E column);
	//
	// Condition<IWheresAnd<E>, IWheresAnd<E>> andWheresOr(E column);

	HOLDER endSubWhere();

}
