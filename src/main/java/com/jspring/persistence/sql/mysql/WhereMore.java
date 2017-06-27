package com.jspring.persistence.sql.mysql;

import com.jspring.persistence.sql.ICondition;
import com.jspring.persistence.sql.IOrdering;
import com.jspring.persistence.sql.ISqlQueryBuilder;
import com.jspring.persistence.sql.ISubWhere;
import com.jspring.persistence.sql.IWhere;
import com.jspring.persistence.sql.IWhereAnd;
import com.jspring.persistence.sql.IWhereOr;
import com.jspring.persistence.sql.IWhereable;

class WhereMore<E extends Enum<?>> implements IWhere<E> {

	//////////////////
	/// FIELDS
	//////////////////
	private IWhereable<E> end;
	private SqlWriter writer;

	public WhereMore(IWhereable<E> end, SqlWriter writer) {
		this.end = end;
		this.writer = writer;
	}

	//////////////////
	/// METHODS
	//////////////////
	@Override
	public IOrdering<E> orderBy(E column) {
		return end.orderBy(column);
	}

	@Override
	public IWhereable<E> limit(int page, int size) {
		return end.limit(page, size);
	}

	@Override
	public ISqlQueryBuilder commit() {
		return end.commit();
	}

	@Override
	public ICondition<IWhereAnd<E>> and(E column) {
		writer.append(" AND ");
		return new Condition<>(this, writer, column);
	}

	@Override
	public ICondition<ISubWhere<E, IWhereAnd<E>>> andSubWhere(E column) {
		writer.append(" AND (");
		return new Condition<>(new WheresMore<>(this, writer), writer, column);
	}

	@Override
	public ICondition<IWhereOr<E>> or(E column) {
		writer.append(" OR ");
		return new Condition<>(this, writer, column);
	}

	@Override
	public ICondition<ISubWhere<E, IWhereOr<E>>> orSubWhere(E column) {
		writer.append(" OR (");
		return new Condition<>(new WheresMore<>(this, writer), writer, column);
	}

}
