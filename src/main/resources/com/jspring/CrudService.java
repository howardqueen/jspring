//package com.jspring.data;
//
//import java.io.Serializable;
//import java.util.function.UnaryOperator;
//
//public class CrudService<T, D, ID extends Serializable, R extends CrudRepository<T, ID>> extends RetrieveService<T, ID, R> {
//
//	public CrudService() {
//		super();
//	}
//
//	public CrudService(R repository) {
//		super(repository);
//	}
//
//	//////////////////
//	/// INSERT/REPLACE
//	//////////////////
//	public int insertOne(T entity) {
//		return repository.insertOne(entity);
//	}
//
//	public int insertIgnoreOne(T entity) {
//		return repository.insertIgnoreOne(entity);
//	}
//
//	public int replaceOne(T entity) {
//		return repository.replaceOne(entity);
//	}
//
//	public int insertAllNoneGeneric(Object[] entities) {
//		return repository.insertAllNoneGeneric(entities);
//	}
//
//	public int insertAll(T[] entities) {
//		return repository.insertAll(entities);
//	}
//
//	public int insertIgnoreAllNoneGeneric(Object[] entities) {
//		return repository.insertIgnoreAllNoneGeneric(entities);
//	}
//
//	public int insertIgnoreAll(T[] entities) {
//		return repository.insertIgnoreAll(entities);
//	}
//
//	public int replaceAllNoneGeneric(Object[] entities) {
//		return repository.replaceAllNoneGeneric(entities);
//	}
//
//	public int replaceAll(T[] entities) {
//		return repository.replaceAll(entities);
//	}
//
//	public int insertOneSkipNull(UnaryOperator<String> map) {
//		return repository.insertOneSkipNull(map);
//	}
//
//	public int insertIgnoreOneSkipNull(UnaryOperator<String> map) {
//		return repository.insertIgnoreOneSkipNull(map);
//	}
//
//	public int replaceOneSkipNull(UnaryOperator<String> map) {
//		return repository.replaceOneSkipNull(map);
//	}
//
//	//////////////////
//	/// UPDATE
//	//////////////////
//	public int updateOne(T entity) {
//		return repository.updateOne(entity);
//	}
//
//	public int updateOneSkipNull(UnaryOperator<String> map, ID id) {
//		return repository.updateOneSkipNull(map, id);
//	}
//
//	public int updateOneSkipNullByMapId(UnaryOperator<String> map, String id) {
//		return repository.updateOneSkipNullByMapId(map, id);
//	}
//
//	public int updateAllSkipNull(UnaryOperator<String> map, ID[] ids) {
//		return repository.updateAllSkipNull(map, ids);
//	}
//
//	public int updateAllSkipNull(UnaryOperator<String> map, Where... wheres) {
//		return repository.updateAllSkipNull(map, wheres);
//	}
//
//	//////////////////
//	/// DELETE
//	//////////////////
//	public int deleteAll(Where... wheres) {
//		return repository.deleteAll(wheres);
//	}
//
//	public int deleteAllNoneGeneric(String[] ids) {
//		return repository.deleteAllNoneGeneric(ids);
//	}
//
//	public int deleteAll(ID[] ids) {
//		return repository.deleteAll(ids);
//	}
//
//	public int deleteOne(ID id) {
//		return repository.deleteOne(id);
//	}
//
//	public int deleteOneByMapId(String id) {
//		return repository.deleteOneNoneGeneric(id);
//	}
//
//}
