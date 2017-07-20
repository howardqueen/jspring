package com.jspring.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.jspring.data.OrderBy;
import com.jspring.data.Where;

public abstract class DtoService<T, D, ID extends Serializable> implements IReporitory<D, ID> {

	protected abstract IReporitory<T, ID> getRepository();

	protected abstract D parseDto(T entity);

	//////////////////
	/// SELECT ALL
	//////////////////
	@Override
	public Collection<D> findAllList(int page, int size, OrderBy[] orders, Where... wheres) {
		Collection<T> rows = getRepository().findAllList(page, size, orders, wheres);
		return null == rows || rows.isEmpty() ? new ArrayList<>()
				: rows.stream()//
						.map(a -> parseDto(a))//
						.collect(Collectors.toList());
	}

	@Override
	public Long findAllCount(Where... wheres) {
		return getRepository().findAllCount(wheres);
	}

	//////////////////
	/// SELECT ONE
	//////////////////
	@Override
	public D findOne(OrderBy[] orders, Where... wheres) {
		T t = getRepository().findOne(orders, wheres);
		return null == t ? null : parseDto(t);
	}

	@Override
	public D findOne(ID id) {
		T t = getRepository().findOne(id);
		return null == t ? null : parseDto(t);
	}

}
