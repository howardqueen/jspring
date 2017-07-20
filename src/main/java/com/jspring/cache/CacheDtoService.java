package com.jspring.cache;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import com.jspring.data.OrderBy;
import com.jspring.data.Where;
import com.jspring.persistence.DtoService;
import com.jspring.persistence.RestPage;

public abstract class CacheDtoService<T, D, ID extends Serializable> extends DtoService<T, D, ID> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Override
	@Cacheable(value = "jspring")
	public RestPage<D> findAll(int page, int size, OrderBy[] orders, Where... wheres) {
		log.info("[CACHEING]findAll(" + page + ", " //
				+ size + ", orders[" + orders.length + "], wheres[" //
				+ wheres.length + "]) ...");
		return super.findAll(page, size, orders, wheres);
	}

	@Override
	@Cacheable(value = "jspring")
	public D findOne(OrderBy[] orders, Where... wheres) {
		log.info("[CACHEING]findOne(orders[" + orders.length + "], wheres[" //
				+ wheres.length + "]) ...");
		return super.findOne(orders, wheres);
	}

	@Override
	@Cacheable(value = "jspring")
	public D findOne(ID id) {
		log.info("[CACHEING]findOne(\"" + String.valueOf(id) + "\") ...");
		return super.findOne(id);
	}

}
