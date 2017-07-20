package com.jspring.persistence;

import java.io.Serializable;

import com.jspring.Environment;
import com.jspring.data.SqlExecutor;
import com.jspring.data.Where;

public class CrudRepository<T, ID extends Serializable> extends Repository<T, ID> implements ICrudReporitory<T, ID> {
	public CrudRepository() {
		super();
	}

	public CrudRepository(SqlExecutor sqlExecutor, Class<T> domain) {
		super(sqlExecutor, domain);
	}

	//////////////////
	/// INSERT/REPLACE
	//////////////////
	@Override
	public int insertOne(T entity) {
		return sql()//
				.insert()//
				.values(getTable().getInsertableColumns(), entity)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	@Override
	public int insertIgnoreOne(T entity) {
		return sql()//
				.insertIgnore()//
				.values(getTable().getInsertableColumns(), entity)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	@Override
	public int replaceOne(T entity) {
		return sql()//
				.replace()//
				.values(getTable().getInsertableColumns(), entity)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	@Override
	public int insertAll(T[] entities) {
		return sql()//
				.insert()//
				.values(getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	@Override
	public int insertIgnoreAll(T[] entities) {
		return sql()//
				.insertIgnore()//
				.values(getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	@Override
	public int replaceAll(T[] entities) {
		return sql()//
				.replace()//
				.values(getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	//////////////////
	/// UPDATE
	//////////////////
	@Override
	public int updateOne(T entity) {
		return sql()//
				.update()//
				.setWithKey(entity, getTable().primaryKey, getTable().getUpdatableColumns())//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	@Override
	public int updateOneSkipNull(T entity) {
		return sql()//
				.update()//
				.setSkipNullWithKey(entity, getTable().primaryKey, getTable().getUpdatableColumns())//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	//////////////////
	/// DELETE
	//////////////////
	@Override
	public int deleteAll(Where... wheres) {
		return sql()//
				.delete()//
				.where(wheres)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	@Override
	public int deleteAll(ID[] ids) {
		return deleteAll(Where.of(getTable().primaryKey).in(ids));
	}

	@Override
	public int deleteOne(ID id) {
		return deleteAll(Where.of(getTable().primaryKey).equalWith(id));
	}

	//////////////////
	/// CREATE/DROP
	//////////////////
	@Override
	public int createIfNotExists() {
		return executeNoneQuery("CREATE TABLE IF NOT EXISTS " + getTable().getDefinition());
	}

	@Override
	public int dropIfExists() {
		return executeNoneQuery("DROP TABLE IF EXISTS `" + getTable().getName() + "`");
	}

	//////////////////
	/// LOAD
	//////////////////
	@Override
	public int loadData(String filename) {
		boolean isLinuxOrWindows = Environment.NewLine.equals("\n");
		String sql = "LOAD DATA INFILE \""//
				+ (isLinuxOrWindows ? filename : filename.substring(1))//
				+ "\" INTO TABLE " + getTable().getName() //
				+ " character set utf8"//
				+ " FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '"//
				+ Environment.NewLine + "';";
		return executeNoneQuery(sql);
	}

}