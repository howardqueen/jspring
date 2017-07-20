package com.jspring.persistence;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import com.jspring.data.Where;

public interface IMapCrudService<T, ID extends Serializable>
		extends IMapService<T, ID, CrudRepository<T, ID>> {

	int insertAllNoneGeneric(Object[] entities);

	int insertIgnoreAllNoneGeneric(Object[] entities);

	int replaceAllNoneGeneric(Object[] entities);

	int insertOneSkipNull(UnaryOperator<String> map);

	int insertIgnoreOneSkipNull(UnaryOperator<String> map);

	int replaceOneSkipNull(UnaryOperator<String> map);
	//////////////////
	/// UPDATE
	//////////////////

	int updateOneSkipNull(UnaryOperator<String> map, ID id);

	int updateOneSkipNullByMapId(UnaryOperator<String> map, String id);

	int updateAllSkipNull(UnaryOperator<String> map, ID[] ids);

	int updateAllSkipNull(UnaryOperator<String> map, Where... wheres);
	//////////////////
	/// DELETE
	//////////////////

	int deleteAllNoneGeneric(String[] ids);

	int deleteOneNoneGeneric(String id);



}