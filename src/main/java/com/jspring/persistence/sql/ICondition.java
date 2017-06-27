package com.jspring.persistence.sql;

import java.util.List;

public interface ICondition<HOLDER> {

	//////////////////
	/// METHODS
	//////////////////
	public HOLDER equalWith(Object value);

	public HOLDER notEqualWith(Object value);

	public HOLDER greaterWith(Object value);

	public HOLDER greaterOrEqualWith(Object value);

	public HOLDER smallerWith(Object value);

	public HOLDER smallerOrEqualWith(Object value);

	public HOLDER isNull();

	public HOLDER notNull();

	public HOLDER inArrayWith(List<Object> value);

	public HOLDER inArrayWith(Object[] value);
}
