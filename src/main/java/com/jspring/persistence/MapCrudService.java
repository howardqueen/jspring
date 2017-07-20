package com.jspring.persistence;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import com.jspring.data.SqlExecutor;
import com.jspring.data.Where;

public class MapCrudService<T, ID extends Serializable> extends MapService<T, ID, CrudRepository<T, ID>>
		implements IMapCrudService<T, ID> {

	public MapCrudService(SqlExecutor sqlExecutor, Class<T> domain) {
		super(new CrudRepository<T, ID>(sqlExecutor, domain));
	}

	//////////////////
	/// INSERT/REPLACE
	//////////////////
	@Override
	public int insertAllNoneGeneric(Object[] entities) {
		return repository.sql()//
				.insert()//
				.values(repository.getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> repository.executeNoneQuery(a, b));
	}

	@Override
	public int insertIgnoreAllNoneGeneric(Object[] entities) {
		return repository.sql()//
				.insertIgnore()//
				.values(repository.getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> repository.executeNoneQuery(a, b));
	}

	@Override
	public int replaceAllNoneGeneric(Object[] entities) {
		return repository.sql()//
				.replace()//
				.values(repository.getTable().getInsertableColumns(), entities)//
				.executeArgsNotNull((a, b) -> repository.executeNoneQuery(a, b));
	}

	@Override
	public int insertOneSkipNull(UnaryOperator<String> map) {
		return repository.sql()//
				.insert()//
				.valuesSkipNull(map, repository.getTable().getInsertableColumns())//
				.executeArgsNotNull((a, b) -> repository.executeNoneQuery(a, b));
	}

	@Override
	public int insertIgnoreOneSkipNull(UnaryOperator<String> map) {
		return repository.sql()//
				.insertIgnore()//
				.valuesSkipNull(map, repository.getTable().getInsertableColumns())//
				.executeArgsNotNull((a, b) -> repository.executeNoneQuery(a, b));
	}

	@Override
	public int replaceOneSkipNull(UnaryOperator<String> map) {
		return repository.sql()//
				.replace()//
				.valuesSkipNull(map, repository.getTable().getInsertableColumns())//
				.executeArgsNotNull((a, b) -> repository.executeNoneQuery(a, b));
	}

	//////////////////
	/// UPDATE
	//////////////////
	@Override
	public int updateOneSkipNull(UnaryOperator<String> map, ID id) {
		return repository.sql()//
				.update()//
				.setSkipNull(map, repository.getTable().getUpdatableColumns())//
				.where(Where.of(repository.getTable().primaryKey).equalWith(id))//
				.executeArgsNotNull((a, b) -> repository.executeNoneQuery(a, b));
	}

	@Override
	public int updateOneSkipNullByMapId(UnaryOperator<String> map, String id) {
		return repository.sql()//
				.update()//
				.setSkipNull(map, repository.getTable().getUpdatableColumns())//
				.where(Where.of(repository.getTable().primaryKey).equalWith(id))//
				.executeArgsNotNull((a, b) -> repository.executeNoneQuery(a, b));
	}

	@Override
	public int updateAllSkipNull(UnaryOperator<String> map, ID[] ids) {
		return updateAllSkipNull(map, Where.of(repository.getTable().primaryKey).in(ids));
	}

	@Override
	public int updateAllSkipNull(UnaryOperator<String> map, Where... wheres) {
		return repository.sql()//
				.update()//
				.setSkipNull(map, repository.getTable().getUpdatableColumns())//
				.whereNotNull(wheres)//
				.executeArgsNotNull((a, b) -> repository.executeNoneQuery(a, b));
	}

	//////////////////
	/// DELETE
	//////////////////
	@Override
	public int deleteAllNoneGeneric(String[] ids) {
		return repository.deleteAll(Where.of(repository.getTable().primaryKey).in(ids));
	}

	@Override
	public int deleteOneNoneGeneric(String id) {
		return repository.deleteAll(Where.of(repository.getTable().primaryKey).equalWith(id));
	}

}