package com.jspring.persistence;

import java.util.Collection;

public class RestPage<T> {
	/**
	 * Current page
	 */
	public Integer page;
	/**
	 * Current page size
	 */
	public Integer size;
	/**
	 * Total number
	 */
	public Long total;
	/**
	 * 
	 */
	public Collection<T> rows;

}
