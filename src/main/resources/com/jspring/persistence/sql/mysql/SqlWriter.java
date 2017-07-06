package com.jspring.persistence.sql.mysql;

import com.jspring.data.JEntity;

final class SqlWriter {

	// //////////////////
	// /// ENTITY CLASS
	// //////////////////
	private final JEntity<?> entityInfo;

	public JEntity<?> getEntityInfo() {
		return entityInfo;
	}

	private StringBuilder sqlWriter;

	public SqlWriter(JEntity<?> entityInfo, StringBuilder sqlWriter) {
		this.entityInfo = entityInfo;
		this.sqlWriter = sqlWriter;
	}

	//////////////////
	/// STRING BUILDER
	//////////////////

	// public StringBuilder getSqlWriter() {
	// return sqlWriter;
	// }

	//////////////////
	/// APPENDER
	//////////////////
	public SqlWriter append(char c) {
		sqlWriter.append(c);
		return this;
	}

	public SqlWriter append(String value) {
		sqlWriter.append(value);
		return this;
	}

}
