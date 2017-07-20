package com.jspring.cache;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import com.jspring.data.OrderBy;
import com.jspring.data.SqlExecutor;
import com.jspring.data.Where;
import com.jspring.persistence.Repository;
import com.jspring.persistence.RestPage;

public class CacheRepository<T, ID extends Serializable> extends Repository<T, ID> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	public CacheRepository() {
		super();
	}

	public CacheRepository(SqlExecutor sqlExecutor, Class<T> domain) {
		super(sqlExecutor, domain);
	}

	@Override
	@Cacheable(value = "jspring")
	public RestPage<T> findAll(int page, int size, OrderBy[] orders, Where... wheres) {
		log.info("[CACHEING]findAll(" + page + ", " + size + ", orders[" + orders.length + "], wheres[" + wheres.length
				+ "]) ...");
		return super.findAll(page, size, orders, wheres);
	}

	@Override
	@Cacheable(value = "jspring")
	public T findOne(OrderBy[] orders, Where... wheres) {
		log.info("[CACHEING]findOne(orders[" + orders.length + "], wheres[" + wheres.length + "]) ...");
		return super.findOne(orders, wheres);
	}

	@Override
	@Cacheable(value = "jspring")
	public T findOne(ID id) {
		log.info("[CACHEING]findOne(\"" + String.valueOf(id) + "\") ...");
		return super.findOne(id);
	}

}