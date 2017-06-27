package com.jspring.persistence.sql.mysql;

import com.jspring.persistence.sql.ISqlUpdateBuilder;
import com.jspring.persistence.sql.IUpdateAnd;
import com.jspring.persistence.sql.IUpdateable;

public class MysqlUpdate<T, E extends Enum<?>> implements IUpdateable<T, E>, ISqlUpdateBuilder {

	@Override
	public String getSqlUpdate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSqlInsert() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSqlReplace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IUpdateAnd<E> update(E column, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISqlUpdateBuilder update(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
