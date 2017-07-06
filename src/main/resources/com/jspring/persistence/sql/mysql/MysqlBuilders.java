package com.jspring.persistence.sql.mysql;

import com.jspring.data.JEntity;
import com.jspring.persistence.sql.ISqlBuilders;
import com.jspring.persistence.sql.IUpdateable;
import com.jspring.persistence.sql.IWhereable;

public class MysqlBuilders implements ISqlBuilders {

	//////////////////
	/// FACTORY
	//////////////////
	public <E extends Enum<?>> IWhereable<E> query(JEntity<?> entityInfo) {
		return new MysqlQuery<>(entityInfo);
	}

	@Override
	public <T, E extends Enum<?>> IUpdateable<T, E> editor(JEntity<?> entityInfo) {
		// TODO Auto-generated method stub
		return null;
	}

}
