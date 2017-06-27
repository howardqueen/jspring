package com.jspring.persistence.sql;

public interface ISqlQueryBuilder {

	String getSqlSelectEntities();

	String getSqlSelectEntity();

	String getSqlSelectPojos(String[] colExps);

	String getSqlSelectPojo(String[] colExps);

	String getSqlSelectObjects(String colExp);

	String getSqlSelectScalar(String colExp);

}
