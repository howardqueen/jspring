package com.jspring.data;

public class CrudIntegerRepository<T> extends CrudRepository<T, Integer> {

	public CrudIntegerRepository() {
		super();
	}

	public CrudIntegerRepository(SqlExecutor sqlExecutor, Class<T> domain) {
		super(sqlExecutor, domain, Integer.class);
	}

}
