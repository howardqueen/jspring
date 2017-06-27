package com.jspring.persistence.sql.mysql;

import com.jspring.data.MetaEntity;
import com.jspring.persistence.sql.ISqlBuilders;
import com.jspring.persistence.sql.IUpdateable;
import com.jspring.persistence.sql.IWhereable;

public class MysqlBuilders implements ISqlBuilders {

	//////////////////
	/// FACTORY
	//////////////////
	public <E extends Enum<?>> IWhereable<E> query(MetaEntity<?> entityInfo) {
		return new MysqlQuery<>(entityInfo);
	}

	@Override
	public <T, E extends Enum<?>> IUpdateable<T, E> editor(MetaEntity<?> entityInfo) {
		// TODO Auto-generated method stub
		return null;
	}

}
