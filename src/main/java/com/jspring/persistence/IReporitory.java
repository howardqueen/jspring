package com.jspring.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.jspring.data.OrderBy;
import com.jspring.data.Where;

public interface IReporitory<T, ID extends Serializable> {

	//////////////////
	/// SELECT ALL
	//////////////////
	Collection<T> findAllList(int page, int size, OrderBy[] orders, Where... wheres);

	default Collection<T> findAllList(int page, int size, OrderBy order, Where... wheres) {
		return findAllList(page, size, new OrderBy[] { order }, wheres);
	}

	default Collection<T> findAllList(int page, int size, Where... wheres) {
		return findAllList(page, size, new OrderBy[0], wheres);
	}

	Long findAllCount(Where... wheres);

	default RestPage<T> findAll(int page, int size, OrderBy[] orders, Where... wheres) {
		RestPage<T> p = new RestPage<>();
		p.total = findAllCount(wheres);
		p.rows = p.total > 0 ? findAllList(page, size, orders, wheres) : new ArrayList<>();
		p.size = size;
		p.page = page;
		return p;
	}

	default RestPage<T> findAll(int page, int size, OrderBy order, Where... wheres) {
		return findAll(page, size, new OrderBy[] { order }, wheres);
	}

	default RestPage<T> findAll(int page, int size, Where... wheres) {
		return findAll(page, size, new OrderBy[0], wheres);
	}

	//////////////////
	/// SELECT ONE
	//////////////////
	T findOne(OrderBy[] orders, Where... wheres);

	default T findOne(OrderBy order, Where... wheres) {
		return findOne(new OrderBy[] { order }, wheres);
	}

	default T findOne(Where... wheres) {
		return findOne(new OrderBy[0], wheres);
	}

	T findOne(ID id);

}
