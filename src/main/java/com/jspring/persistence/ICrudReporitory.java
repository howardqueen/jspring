package com.jspring.persistence;

import java.io.Serializable;

import com.jspring.data.Where;

public interface ICrudReporitory<T, ID extends Serializable> extends IReporitory<T, ID> {

	//////////////////
	/// INSERT/REPLACE
	//////////////////
	int insertOne(T entity);

	int insertIgnoreOne(T entity);

	int replaceOne(T entity);

	int insertAll(T[] entities);

	int insertIgnoreAll(T[] entities);

	int replaceAll(T[] entities);

	//////////////////
	/// UPDATE
	//////////////////
	int updateOne(T entity);

	int updateOneSkipNull(T entity);

	//////////////////
	/// DELETE
	//////////////////
	int deleteAll(Where... wheres);

	int deleteAll(ID[] ids);

	int deleteOne(ID id);

	//////////////////
	/// CREATE/DROP
	//////////////////
	int createIfNotExists();

	int dropIfExists();

	//////////////////
	/// LOAD
	//////////////////
	int loadData(String filename);
}