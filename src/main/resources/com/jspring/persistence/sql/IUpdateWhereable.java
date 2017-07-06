package com.jspring.persistence.sql;

public interface IUpdateWhereable<E extends Enum<?>> extends IUpdateCommit {

	ICondition<IUpdateWhereable<E>> and(E column);

}
