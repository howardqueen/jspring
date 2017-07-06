package com.jspring.persistence.sql.mysql;

import com.jspring.persistence.sql.ILimitable;
import com.jspring.persistence.sql.IOrderAnd;
import com.jspring.persistence.sql.IOrdering;
import com.jspring.persistence.sql.ISqlQueryBuilder;
import com.jspring.persistence.sql.IWhereable;

class OrderAnd<E extends Enum<?>> implements IOrderAnd<E> {

	private final ILimitable<E> end;

	private final SqlWriter writer;

	public OrderAnd(ILimitable<E> end, SqlWriter writer) {
		this.end = end;
		this.writer = writer;
	}

	@Override
	public IOrdering<E> and(E column) {
		writer.append(',');
		return new Ordering<E>(this, writer, column);
	}

	@Override
	public IWhereable<E> limit(int page, int size) {
		return end.limit(page, size);
	}

	@Override
	public ISqlQueryBuilder commit() {
		return end.commit();
	}

}
