package com.jspring.persistence;

import java.io.Serializable;
import java.util.Collection;

public interface IMapService<T, ID extends Serializable, R extends IReporitory<T, ID>> {

	T findOneNoneGeneric(String id);

	Collection<JoinOptionItem> findOptions(String optionDomain, int size);

}