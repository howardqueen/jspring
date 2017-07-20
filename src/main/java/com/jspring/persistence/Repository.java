package com.jspring.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.jspring.data.OrderBy;
import com.jspring.data.SqlBuilder;
import com.jspring.data.SqlExecutor;
import com.jspring.data.Where;

public class Repository<T, ID extends Serializable> implements IReporitory<T, ID> {

	//////////////////
	/// FIELDS
	//////////////////
	@Autowired
	private SqlExecutor _sqlExecutor;

	protected SqlExecutor getSqlExecutor() {
		return _sqlExecutor;
	}

	private final JTableValue _table;

	public JTableValue getTable() {
		return _table;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getDomain() {
		return (Class<T>) getTable().domain;
	}

	protected SqlBuilder sql() {
		return new SqlBuilder(getTable());
	}

	public Repository() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		_table = JTableValue.of((Class<?>) params[0]);
	}

	public Repository(SqlExecutor sqlExecutor, Class<T> domain) {
		this._sqlExecutor = sqlExecutor;
		_table = JTableValue.of(domain);
	}

	//////////////////
	/// METHODS
	//////////////////
	/**
	 * 获取实体列表
	 * 
	 */
	protected <E> Collection<E> queryEntities(String sql, Object... args) {
		return getSqlExecutor().queryEntities(getTable().database, getTable().getFindAllableColumns(), sql, args);
	}

	/**
	 * 获取首个实体
	 */
	protected <E> E queryEntity(String sql, Object... args) {
		return getSqlExecutor().queryEntity(getTable().database, getTable().getFindOnableColumns(), sql, args);
	}

	/**
	 * 获取实体快照列表，可用于统计查询等
	 * 
	 * @param pojoClass
	 *            不支持 @Table @Column 等
	 */
	protected <J> Collection<J> queryPojos(Class<J> pojoClass, String sql, Object... args) {
		return getSqlExecutor().queryPojos(getTable().database, pojoClass, sql, args);
	}

	/**
	 * 获取实体快照，可用于统计查询等
	 * 
	 * @param pojoClass
	 *            不支持 @Table @Column 等
	 */
	protected <J> J queryPojo(Class<J> pojoClass, String sql, Object... args) {
		return getSqlExecutor().queryPojo(getTable().database, pojoClass, sql, args);
	}

	/**
	 * 获取基础类型的列表
	 * 
	 * @param primitiveType
	 */
	protected <P> Collection<P> queryPrimitives(Class<P> primitiveType, String sql, Object... args) {
		return getSqlExecutor().queryPrimitives(getTable().database, primitiveType, sql, args);
	}

	/**
	 * 获取基础类型的值
	 * 
	 * @param primitiveType
	 */
	protected <P> P queryPrimitive(Class<P> primitiveType, String sql, Object... args) {
		return getSqlExecutor().queryPrimitive(getTable().database, primitiveType, sql, args);
	}

	/**
	 * 执行并返回影响行数
	 * 
	 */
	protected int executeNoneQuery(String sql, Object... args) {
		return getSqlExecutor().executeNoneQuery(getTable().database, sql, args);
	}

	//////////////////
	/// SELECT ALL
	//////////////////
	@Override
	public Collection<T> findAllList(int page, int size, OrderBy[] orders, Where... wheres) {
		return sql()//
				.select(getTable().getFindAllableColumns())//
				.where(wheres)//
				.orderBy(orders)//
				.limit(page, size)//
				.execute(a -> queryEntities(a), //
						(a, b) -> queryEntities(a, b));
	}

	@Override
	public Long findAllCount(Where... wheres) {
		return sql()//
				.select("COUNT(0)")//
				.where(wheres)//
				.execute(a -> queryPrimitive(Long.class, a), //
						(a, b) -> queryPrimitive(Long.class, a, b));
	}

	//////////////////
	/// SELECT ONE
	//////////////////
	@Override
	public T findOne(OrderBy[] orders, Where... wheres) {
		return sql()//
				.select(getTable().getFindOnableColumns())//
				.where(wheres)//
				.orderBy(orders)//
				.limit(1, 1)//
				.execute(a -> queryEntity(a), //
						(a, b) -> queryEntity(a, b));
	}

	@Override
	public T findOne(ID id) {
		return sql()//
				.select(getTable().getFindOnableColumns())//
				.where(Where.of(getTable().primaryKey).equalWith(id))//
				.limit(1, 1).executeArgsNotNull((a, b) -> queryEntity(a, b));
	}

}