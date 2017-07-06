package com.jspring.persistence.sql.mysql;

import com.jspring.persistence.sql.IOrderAnd;
import com.jspring.persistence.sql.IOrdering;

class Ordering<E extends Enum<?>> implements IOrdering<E> {

	private final IOrderAnd<E> end;
	private final SqlWriter writer;

	public Ordering(IOrderAnd<E> end, SqlWriter writer, Enum<?> column) {
		this.end = end;
		this.writer = writer;
		writer//
				.append('`').append(writer.getEntityInfo().getSqlTableName()).append('`').append('.')//
				.append('`').append(writer.getEntityInfo().getJField(column).getName4SQL()).append('`');
	}

	//////////////////
	/// METHODS
	//////////////////
	@Override
	public IOrderAnd<E> ascending() {
		writer.append(" ASC ");
		return end;
	}

	@Override
	public IOrderAnd<E> descending() {
		writer.append(" DESC ");
		return end;
	}
	
}
