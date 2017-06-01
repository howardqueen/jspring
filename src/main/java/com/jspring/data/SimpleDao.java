package com.jspring.data;

import java.util.List;

public final class SimpleDao<T> {

	private final Dao<T> source;

	//////////////////
	/// CONSTRUCTOR
	//////////////////
	public SimpleDao(Dao<T> source) {
		this.source = source;
	}

	//////////////////
	/// CRUD VIEW
	//////////////////
	public CrudTableInfo getCrudView() {
		return source.getCrudView();
	}

	//////////////////
	///
	//////////////////
	public List<T> findAllBySQL(String sql, Object... args) {
		return source.findAllBySQL(sql, args);
	}

	public T findOneBySQL(String sql, Object... args) {
		return source.findOneBySQL(sql, args);
	}

	public int execute(String sql, Object... args) {
		return source.execute(sql, args);
	}

	//////////////////
	///
	//////////////////
	public int countAllBySQL(String sql, Object... args) {
		return source.countAllBySQL(sql, args);
	}

	public <E> List<E> findAllBySQL(final Class<E> domainClass, String sql, Object... args) {
		return source.findAllBySQL(domainClass, sql, args);
	}

	public <E> E findOneBySQL(Class<E> domainClass, String sql, Object... args) {
		return source.findOneBySQL(domainClass, sql, args);
	}

	//////////////////
	/// RETRIEVE LIST
	//////////////////
	public List<T> findAll(int page, int size) {
		return source.findAll(page, size);
	}

	public List<T> findAll(int page, int size, DaoWhere... wheres) {
		return source.findAll(page, size, wheres);
	}

	public List<T> findAll(int page, int size, DaoOrder order, DaoWhere... wheres) {
		return source.findAll(page, size, order, wheres);
	}

	/**
	 * 
	 * @param page
	 * @param size
	 * @param orders
	 * @param wheres
	 * @return
	 */
	public List<T> findAll(int page, int size, DaoOrder[] orders, DaoWhere... wheres) {
		return source.findAll(page, size, orders, wheres);
	}

	public int countAll() {
		return source.countAll();
	}

	/**
	 * 
	 * @param wheres
	 * @return
	 */
	public int countAll(DaoWhere... wheres) {
		return source.countAll(wheres);
	}

	//////////////////
	/// RETRIEVE ONE
	//////////////////
	public T findOne(DaoWhere... wheres) {
		return source.findOne(wheres);
	}

	public T findOne(DaoOrder order, DaoWhere... wheres) {
		return source.findOne(order, wheres);
	}

	public T findOne(DaoOrder[] orders, DaoWhere... wheres) {
		return source.findOne(orders, wheres);
	}

	/**
	 * 
	 * @param idValue
	 * @param partitionDate
	 * @return
	 */
	public T findOne(String idValue) {
		return source.findOne(idValue, null);
	}

	public T findOne(Integer idValue) {
		return source.findOne(idValue, null);
	}

	public T findOne(Long idValue) {
		return source.findOne(idValue, null);
	}

	//////////////////
	///
	//////////////////
	public T insertAndGet(T entity) {
		return source.insertAndGet(entity);
	}

	public T replaceAndGet(T entity) {
		return source.replaceAndGet(entity);
	}

	public int insert(@SuppressWarnings("unchecked") T... entities) {
		return source.insert(entities);
	}

	public int replace(@SuppressWarnings("unchecked") T... entities) {
		return source.replace(entities);
	}

	//////////////////
	///
	//////////////////
	/**
	 * 
	 * @param entity
	 * @param idValue
	 * @return
	 */
	public T updateAndGet(T entity, String idValue) {
		return source.updateAndGet(entity, idValue);
	}

	public T updateAndGet(T entity) {
		return source.updateAndGet(entity);
	}

	/**
	 * 
	 * @param entity
	 * @param idValue
	 * @return
	 */
	public int update(T entity, String idValue) {
		return source.update(entity, idValue);
	}

	public int update(T entity) {
		return source.update(entity);
	}

	//////////////////
	///
	//////////////////
	/**
	 * 
	 * @param wheres
	 * @return
	 */
	public int deleteAll(DaoWhere... wheres) {
		return source.deleteAll(wheres);
	}

	/**
	 * 
	 * @param idValue
	 * @param partitionDate
	 * @return
	 */
	public int delete(String idValue) {
		return source.delete(idValue, null);
	}

	public int delete(Integer idValue) {
		return source.delete(idValue, null);
	}

	public int delete(Long idValue) {
		return source.delete(idValue, null);
	}

	//////////////////
	///
	//////////////////
	public void loadCsv(String csvFilename) {
		source.loadCsv(csvFilename, null);
	}

	public void dropIfExists() {
		source.dropIfExists(null);
	}

	public void createIfNotExists() {
		source.createIfNotExists(null);
	}

}
