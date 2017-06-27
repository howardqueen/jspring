package com.jspring.persistence.sql;

public interface ISqlUpdateBuilder {

	String getSqlUpdate();
	
	String getSqlInsert();
	
	String getSqlReplace();

}
