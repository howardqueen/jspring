package com.jspring.persistence.sql;

import com.jspring.data.MetaEntity;

public interface ISqlBuilders {

	<E extends Enum<?>> IWhereable<E> query(MetaEntity<?> entityInfo);

	<T, E extends Enum<?>> IUpdateable<T, E> editor(MetaEntity<?> entityInfo);

}
