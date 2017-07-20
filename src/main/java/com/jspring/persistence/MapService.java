package com.jspring.persistence;

import java.io.Serializable;
import java.util.Collection;

import com.jspring.Strings;
import com.jspring.data.SqlExecutor;
import com.jspring.data.Where;

public class MapService<T, ID extends Serializable, R extends Repository<T, ID>> implements IMapService<T, ID, R> {

	public final R repository;

	public MapService(R repository) {
		this.repository = repository;
	}

	@SuppressWarnings("unchecked")
	public MapService(SqlExecutor sqlExecutor, Class<T> domain) {
		this.repository = (R) new Repository<T, ID>(sqlExecutor, domain);
	}

	//////////////////
	/// CONF
	//////////////////
	@Override
	public T findOneNoneGeneric(String id) {
		return repository.sql()//
				.select(repository.getTable().getFindOnableColumns())//
				.where(Where.of(repository.getTable().primaryKey).equalWith(id))//
				.limit(1, 1)//
				.executeArgsNotNull((a, b) -> repository.queryEntity(a, b));
	}

	@Override
	public Collection<JoinOptionItem> findOptions(String optionDomain, int size) {
		JoinOptions jo = repository.getTable().getOptions(optionDomain);
		if (Strings.isNullOrEmpty(jo.schema())) {
			return repository.queryPojos(JoinOptionItem.class, "SELECT `" + jo.valueColumn() + "` AS `value`, `"
					+ jo.textColumn() + "` AS `text` FROM `" + jo.name() + "` LIMIT 1," + size);
		}
		return repository.queryPojos(JoinOptionItem.class, "SELECT `" + jo.valueColumn() + "` AS `value`, `"
				+ jo.textColumn() + "` AS `text` FROM `" + jo.schema() + "`.`" + jo.name() + "` LIMIT 1," + size);
	}

}