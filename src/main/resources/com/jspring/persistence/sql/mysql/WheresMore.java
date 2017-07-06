package com.jspring.persistence.sql.mysql;

import com.jspring.persistence.sql.ICondition;
import com.jspring.persistence.sql.ISubWhere;
import com.jspring.persistence.sql.ISubWhereAnd;
import com.jspring.persistence.sql.ISubWhereOr;

class WheresMore<E extends Enum<?>, HOLDER> implements ISubWhere<E, HOLDER> {

	//////////////////
	/// FIELDS
	//////////////////
	private final HOLDER end;
	private final SqlWriter writer;

	public WheresMore(HOLDER end, SqlWriter writer) {
		this.end = end;
		this.writer = writer;
	}

	//////////////////
	/// METHODS
	//////////////////
	@Override
	public ICondition<ISubWhereAnd<E, HOLDER>> and(E column) {
		writer.append(" AND ");
		return new Condition<ISubWhereAnd<E, HOLDER>>(this, writer, column);
	}

	@Override
	public ICondition<ISubWhereOr<E, HOLDER>> or(E column) {
		writer.append(" OR ");
		return new Condition<ISubWhereOr<E, HOLDER>>(this, writer, column);
	}

	@Override
	public HOLDER endSubWhere() {
		writer.append(')');
		return end;
	}

}
