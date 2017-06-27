package com.jspring.persistence.sql.mysql;

import java.util.List;

import com.jspring.data.MetaEntity;
import com.jspring.persistence.sql.ICondition;

class Condition<HOLDER> implements ICondition<HOLDER> {

	//////////////////
	/// FIELDS
	//////////////////
	private final HOLDER end;

	private final SqlWriter writer;

	public Condition(HOLDER end, SqlWriter writer, Enum<?> column) {
		this.end = end;
		this.writer = writer;
		MetaEntity<?> ei = writer.getEntityInfo();
		writer.append('`').append(ei.getSqlTableName()).append('`')//
				.append('.')//
				.append('`').append(ei.getMetaField(column).getSqlColumnName()).append('`');
	}

	//////////////////
	/// METHODS
	//////////////////
	@Override
	public HOLDER equalWith(Object value) {
		writer.append('=').append('"').append(String.valueOf(value)).append('"');
		return end;
	}

	@Override
	public HOLDER notEqualWith(Object value) {
		writer.append('!').append('=').append('"').append(String.valueOf(value)).append('"');
		return end;
	}

	@Override
	public HOLDER greaterWith(Object value) {
		writer.append('>').append('"').append(String.valueOf(value)).append('"');
		return end;
	}

	@Override
	public HOLDER greaterOrEqualWith(Object value) {
		writer.append('>').append('=').append('"').append(String.valueOf(value)).append('"');
		return end;
	}

	@Override
	public HOLDER smallerWith(Object value) {
		writer.append('<').append('"').append(String.valueOf(value)).append('"');
		return end;
	}

	@Override
	public HOLDER smallerOrEqualWith(Object value) {
		writer.append('<').append('=').append('"').append(String.valueOf(value)).append('"');
		return end;
	}

	@Override
	public HOLDER isNull() {
		writer.append("NOT NULL");
		return end;
	}

	@Override
	public HOLDER notNull() {
		writer.append("IS NULL");
		return end;
	}

	@Override
	public HOLDER inArrayWith(List<Object> value) {
		return end;
	}

	@Override
	public HOLDER inArrayWith(Object[] value) {
		return end;
	}

}
