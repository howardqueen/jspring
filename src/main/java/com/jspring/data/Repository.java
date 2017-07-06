package com.jspring.data;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class Repository<T, ID extends Serializable> {

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

	private final Class<ID> _idClass;

	public Class<ID> getIdClass() {
		return _idClass;
	}

	protected SqlBuilder sql() {
		return new SqlBuilder(getTable());
	}

	@SuppressWarnings("unchecked")
	public Repository() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		_table = JTableValue.of((Class<?>) params[0]);
		_idClass = (Class<ID>) params[1];
	}

	public Repository(SqlExecutor sqlExecutor, Class<T> domain, Class<ID> idClass) {
		this._sqlExecutor = sqlExecutor;
		_table = JTableValue.of(domain);
		_idClass = idClass;
	}

	//////////////////
	/// METHODS
	//////////////////
	/**
	 * 获取实体列表
	 * 
	 */
	public <E> List<E> queryEntities(JColumnValue[] columns, String sql, Object... args) {
		return getSqlExecutor().queryEntities(getTable().schema, columns, sql, args);
	}

	/**
	 * 获取首个实体
	 */
	public <E> E queryEntity(JColumnValue[] columns, String sql, Object... args) {
		return getSqlExecutor().queryEntity(getTable().schema, columns, sql, args);
	}

	/**
	 * 获取实体快照列表，可用于统计查询等
	 * 
	 * @param pojoClass
	 *            不支持 @Table @Column 等
	 */
	public <J> List<J> queryPojos(Class<J> pojoClass, String sql, Object... args) {
		return getSqlExecutor().queryPojos(getTable().schema, pojoClass, sql, args);
	}

	/**
	 * 获取实体快照，可用于统计查询等
	 * 
	 * @param pojoClass
	 *            不支持 @Table @Column 等
	 */
	public <J> J queryPojo(Class<J> pojoClass, String sql, Object... args) {
		return getSqlExecutor().queryPojo(getTable().schema, pojoClass, sql, args);
	}

	/**
	 * 获取基础类型的列表
	 * 
	 * @param primitiveType
	 */
	public <P> List<P> queryPrimitives(Class<P> primitiveType, String sql, Object... args) {
		return getSqlExecutor().queryPrimitives(getTable().schema, primitiveType, sql, args);
	}

	/**
	 * 获取基础类型的值
	 * 
	 * @param primitiveType
	 */
	public <P> P queryPrimitive(Class<P> primitiveType, String sql, Object... args) {
		return getSqlExecutor().queryPrimitive(getTable().schema, primitiveType, sql, args);
	}

	/**
	 * 执行并返回影响行数
	 * 
	 */
	public int executeNoneQuery(String sql, Object... args) {
		return getSqlExecutor().executeNoneQuery(getTable().schema, sql, args);
	}

	//////////////////
	/// SELECT ALL
	//////////////////

	public List<T> findAll(int page, int size, OrderBy[] orders, Where... wheres) {
		return sql()//
				.select(getTable().getFindAllableColumns())//
				.where(wheres)//
				.orderBy(orders)//
				.limit(page, size)//
				.execute(a -> queryEntities(getTable().getFindAllableColumns(), a), //
						(a, b) -> queryEntities(getTable().getFindAllableColumns(), a, b));
	}

	public List<T> findAll(int page, int size, OrderBy order, Where... wheres) {
		return findAll(page, size, new OrderBy[] { order }, wheres);
	}

	public List<T> findAll(int page, int size, Where... wheres) {
		return findAll(page, size, new OrderBy[0], wheres);
	}

	public Long countAll(Where... wheres) {
		return sql()//
				.select("COUNT(0)")//
				.where(wheres)//
				.execute(a -> queryPrimitive(Long.class, a), //
						(a, b) -> queryPrimitive(Long.class, a, b));
	}

	//////////////////
	/// SELECT ONE
	//////////////////
	public T findOne(OrderBy[] orders, Where... wheres) {
		return sql()//
				.select(getTable().getFindOnableColumns())//
				.where(wheres)//
				.orderBy(orders)//
				.limit(1, 1)//
				.execute(a -> queryEntity(getTable().getFindOnableColumns(), a), //
						(a, b) -> queryEntity(getTable().getFindOnableColumns(), a, b));
	}

	public T findOne(OrderBy order, Where... wheres) {
		return findOne(new OrderBy[] { order }, wheres);
	}

	public T findOne(Where... wheres) {
		return findOne(new OrderBy[0], wheres);
	}

	public T findOne(ID id) {
		return sql()//
				.select(getTable().getFindOnableColumns())//
				.where(Where.of(getTable().primaryKey).equalWith(id))//
				.limit(1, 1).executeArgsNotNull((a, b) -> queryEntity(getTable().getFindOnableColumns(), a, b));
	}

	public T findOneByKey(String id) {
		return sql()//
				.select(getTable().getFindOnableColumns())//
				.where(Where.of(getTable().primaryKey).equalWith(id))//
				.limit(1, 1).executeArgsNotNull((a, b) -> queryEntity(getTable().getFindOnableColumns(), a, b));
	}

}