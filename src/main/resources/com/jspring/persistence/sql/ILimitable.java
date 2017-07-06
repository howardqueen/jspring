package com.jspring.persistence.sql;

public interface ILimitable<E extends Enum<?>> extends IQueryCommit {

	IWhereable<E> limit(int page, int size);

}
