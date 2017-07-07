package com.jspring.data;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import com.jspring.Environment;

public class CrudRepository<T, ID extends Serializable> extends Repository<T, ID> {

	public CrudRepository() {
		super();
	}

	public CrudRepository(SqlExecutor sqlExecutor, Class<T> domain) {
		super(sqlExecutor, domain);
	}

	//////////////////
	/// INSERT/REPLACE
	//////////////////
	public int insertOne(T entity) {
		return sql()//
				.insert()//
				.values(getTable().getInsertableColumns(), entity)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int insertIgnoreOne(T entity) {
		return sql()//
				.insertIgnore()//
				.values(getTable().getInsertableColumns(), entity)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int replaceOne(T entity) {
		return sql()//
				.replace()//
				.values(getTable().getInsertableColumns(), entity)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int insertAllEntities(Object[] entities) {
		return sql()//
				.insert()//
				.values(getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int insertAll(T[] entities) {
		return sql()//
				.insert()//
				.values(getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int insertIgnoreAllEntities(Object[] entities) {
		return sql()//
				.insertIgnore()//
				.values(getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int insertIgnoreAll(T[] entities) {
		return sql()//
				.insertIgnore()//
				.values(getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int replaceAllEntities(Object[] entities) {
		return sql()//
				.replace()//
				.values(getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int replaceAll(T[] entities) {
		return sql()//
				.replace()//
				.values(getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int insertOneSkipNull(UnaryOperator<String> map) {
		return sql()//
				.insert()//
				.valuesSkipNull(map, getTable().getInsertableColumns())//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int insertIgnoreOneSkipNull(UnaryOperator<String> map) {
		return sql()//
				.insertIgnore()//
				.valuesSkipNull(map, getTable().getInsertableColumns())//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int replaceOneSkipNull(UnaryOperator<String> map) {
		return sql()//
				.replace()//
				.valuesSkipNull(map, getTable().getInsertableColumns())//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	//////////////////
	/// UPDATE
	//////////////////
	public int updateOne(T entity) {
		return sql()//
				.update()//
				.setWithKey(entity, getTable().primaryKey, getTable().getUpdatableColumns())//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int updateOneSkipNull(UnaryOperator<String> map, ID id) {
		return sql()//
				.update()//
				.setSkipNull(map, getTable().getUpdatableColumns())//
				.where(Where.of(getTable().primaryKey).equalWith(id))//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int updateOneSkipNullByKey(UnaryOperator<String> map, String id) {
		return sql()//
				.update()//
				.setSkipNull(map, getTable().getUpdatableColumns())//
				.where(Where.of(getTable().primaryKey).equalWith(id))//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int updateAllSkipNull(UnaryOperator<String> map, ID[] ids) {
		return updateAllSkipNull(map, Where.of(getTable().primaryKey).in(ids));
	}

	public int updateAllSkipNull(UnaryOperator<String> map, Where... wheres) {
		return sql()//
				.update()//
				.setSkipNull(map, getTable().getUpdatableColumns())//
				.whereNotNull(wheres)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	//////////////////
	/// DELETE
	//////////////////
	public int deleteAll(Where... wheres) {
		return sql()//
				.delete()//
				.where(wheres)//
				.executeArgsNotNull((a, b) -> executeNoneQuery(a, b));
	}

	public int deleteAllByKeys(Object[] ids) {
		return deleteAll(Where.of(getTable().primaryKey).in(ids));
	}

	public int deleteAll(ID[] ids) {
		return deleteAll(Where.of(getTable().primaryKey).in(ids));
	}

	public int deleteOne(ID id) {
		return deleteAll(Where.of(getTable().primaryKey).equalWith(id));
	}

	public int deleteOneByKey(String id) {
		return deleteAll(Where.of(getTable().primaryKey).equalWith(id));
	}

	//////////////////
	/// CREATE/DROP
	//////////////////
	public int createIfNotExists() {
		return executeNoneQuery("CREATE TABLE IF NOT EXISTS " + getTable().getDefinition());
	}

	public int dropIfExists() {
		return executeNoneQuery("DROP TABLE IF EXISTS `" + getTable().getName() + "`");
	}

	//////////////////
	/// LOAD
	//////////////////
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