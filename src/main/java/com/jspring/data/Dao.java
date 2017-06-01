package com.jspring.data;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.jspring.Environment;
import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.data.DaoWhere.Operators;
import com.jspring.date.DateTime;

public class Dao<T> {

	private static final Logger log = LoggerFactory.getLogger(Dao.class);

	private SimpleDao<T> simpleDao;

	public SimpleDao<T> getSimpleDao() {
		if (null == simpleDao) {
			simpleDao = new SimpleDao<>(this);
		}
		return simpleDao;
	}

	/**
	 * 获取含表达式的列名，用于计算且获取它的值
	 * 
	 * @param f
	 * @return
	 */
	public static String getSqlColumnValueName(Field f) {
		Column column = f.getAnnotation(Column.class);
		if (null == column) {
			return f.getName();
		}
		if (!Strings.isNullOrEmpty(column.name())) {
			return column.name();
		}
		return f.getName();
	}

	/**
	 * 获取不含表达式的列名（别名部分），仅用于获取它的值
	 * 
	 * @param f
	 * @return
	 */
	public static String getSqlColumnNickName(Field f) {
		Column column = f.getAnnotation(Column.class);
		if (null == column) {
			return f.getName();
		}
		if (!Strings.isNullOrEmpty(column.name()) && column.name().indexOf(' ') < 0) {
			return column.name();
		}
		return f.getName();
	}

	/**
	 * 获取映射到SQL的值
	 * 
	 * @param f
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String getSqlValue(Field f, Object entity) throws IllegalArgumentException, IllegalAccessException {
		Object obj = f.get(entity);
		if (null == obj) {
			Column cl = f.getAnnotation(Column.class);
			if (null == cl || cl.nullable()) {// Nullable
				return "NULL";
			}
			switch (f.getType().getSimpleName()) {
			case ("String"):
				return "''";
			case ("Integer"):
				return "'0'";
			case ("Long"):
				return "'0'";
			case ("Short"):
				return "'0'";
			case ("Double"):
				return "'0'";
			case ("Date"):
				return '"' + DateTime.getNow().toString() + '"';
			case ("Float"):
				return "'0'";
			case ("Boolean"):
				return "'0'";
			default:
				throw new RuntimeException("BaseDao.convert2SQL(): Cannot convert field to SQL object for "
						+ entity.getClass().getSimpleName() + "." + f.getType().getSimpleName());
			}
		}
		if (f.getType().getSimpleName().equals("Date")) {
			return '"' + (new DateTime((Date) obj).toString()) + '"';
		}
		return '"' + String.valueOf(obj) + '"';
	}

	//////////////////
	///
	//////////////////
	protected final Class<T> domainClass;
	protected final String oriTableName;
	protected final JdbcTemplate jdbcTemplate;
	protected final boolean isPartitionDateTable;

	//////////////////
	///
	//////////////////
	protected Field _partitionDateField = null;

	protected String getTableName(DaoWhere[] wheres) {
		if (!isPartitionDateTable) {
			return oriTableName;
		}
		for (DaoWhere w : wheres) {
			if (w.column.equals(getCrudView().partitionDateColumn)) {
				return oriTableName + "_" + DateTime.valueOf(w.value).toShortDateString();
			}
		}
		throw Exceptions
				.newInstance("[PartitionDateColumn]" + getCrudView().partitionDateColumn + " cannot be null or empty.");
	}

	protected String getTableName(T entity)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (!isPartitionDateTable) {
			return oriTableName;
		}
		if (null == _partitionDateField) {
			_partitionDateField = domainClass.getField(getCrudView().partitionDateColumn);
		}
		DateTime partitionDate = (DateTime) _partitionDateField.get(entity);
		if (null == partitionDate) {
			throw Exceptions.newNullArgumentException("entity." + getCrudView().partitionDateColumn);
		}
		return oriTableName + "_" + partitionDate.toShortDateString();
	}

	protected String getTableName(DateTime partitionDate) {
		if (!isPartitionDateTable) {
			return oriTableName;
		}
		if (null == partitionDate) {
			throw Exceptions.newNullArgumentException("partitionDate");
		}
		return oriTableName + "_" + partitionDate.toShortDateString();
	}

	protected DateTime getPartitionDate(T entity)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (!isPartitionDateTable) {
			return null;
		}
		if (null == _partitionDateField) {
			_partitionDateField = domainClass.getField(getCrudView().partitionDateColumn);
		}
		return (DateTime) _partitionDateField.get(entity);
	}

	private boolean _idColumnInit = false;
	private Field _idColumn = null;

	protected Field getIdColumn() {
		if (_idColumnInit) {
			return _idColumn;
		}
		_idColumnInit = true;
		for (Field f : domainClass.getFields()) {
			Id id = f.getAnnotation(Id.class);
			if (null != id) {
				_idColumn = f;
				break;
			}
		}
		if (null == _idColumn) {
			_idColumn = domainClass.getFields()[0];
			log.info(domainClass.getName() + ": [Id-Column]" + _idColumn.getName());
		}
		return _idColumn;
	}

	private boolean _idColumnNameInit = false;
	private String _idColumnName = null;

	protected String getIdColumnName() {
		if (_idColumnNameInit) {
			return _idColumnName;
		}
		_idColumnNameInit = true;
		if (null == getIdColumn()) {
			return _idColumnName;
		}
		_idColumnName = getSqlColumnNickName(getIdColumn());
		return _idColumnName;
	}

	private boolean _isIdGenerateIdentityInit = false;
	private boolean _isIdGenerateIdentity = false;

	protected boolean isIdGenerateIdentity() {
		if (_isIdGenerateIdentityInit) {
			return _isIdGenerateIdentity;
		}
		_isIdGenerateIdentityInit = true;
		if (null == getIdColumn()) {
			return _isIdGenerateIdentity;
		}
		GeneratedValue v = getIdColumn().getAnnotation(GeneratedValue.class);
		if (null != v && v.strategy() == GenerationType.IDENTITY) {
			_isIdGenerateIdentity = true;
			log.info(domainClass.getName() + ": [Id-Generate-Identity]" + _idColumn.getName());
		}
		return _isIdGenerateIdentity;
	}

	//////////////////
	/// CRUD VIEW
	//////////////////
	public Dao(DataManager dataManager, Class<T> domainClass) {
		this.domainClass = domainClass;
		CrudTable cv = domainClass.getAnnotation(CrudTable.class);
		this.isPartitionDateTable = (null == cv || Strings.isNullOrEmpty(cv.partitionDateColumn())) ? false : true;
		//
		Table table = domainClass.getAnnotation(Table.class);
		if (null == table) {
			this.jdbcTemplate = dataManager.getSpringDatabase().jdbcTemplate;
			this.oriTableName = domainClass.getSimpleName();
			return;
		}
		this.jdbcTemplate = Strings.isNullOrEmpty(table.catalog()) ? dataManager.getSpringDatabase().jdbcTemplate
				: dataManager.getDatabase(table.catalog()).jdbcTemplate;
		this.oriTableName = Strings.isNullOrEmpty(table.name()) ? domainClass.getSimpleName() : table.name();
		if (!isPartitionDateTable && !Strings.isNullOrEmpty(table.schema())) {
			log.info("CREATE TABLE [" + this.oriTableName + "] ...");
			this.createIfNotExists(null);
		}
	}

	public Dao(DataManager dataManager, Class<T> domainClass, String databaseName, String tableName) {
		this.domainClass = domainClass;
		CrudTable cv = domainClass.getAnnotation(CrudTable.class);
		this.isPartitionDateTable = (null == cv || Strings.isNullOrEmpty(cv.partitionDateColumn())) ? false : true;
		//
		Table table = domainClass.getAnnotation(Table.class);
		if (null == table) {
			this.jdbcTemplate = Strings.isNullOrEmpty(databaseName) ? dataManager.getSpringDatabase().jdbcTemplate
					: dataManager.getDatabase(databaseName).jdbcTemplate;
			this.oriTableName = Strings.isNullOrEmpty(tableName) ? domainClass.getSimpleName() : tableName;
			return;
		}
		if (Strings.isNullOrEmpty(databaseName)) {
			this.jdbcTemplate = Strings.isNullOrEmpty(table.catalog()) ? dataManager.getSpringDatabase().jdbcTemplate
					: dataManager.getDatabase(table.catalog()).jdbcTemplate;
		} else {
			this.jdbcTemplate = dataManager.getDatabase(databaseName).jdbcTemplate;
		}
		if (Strings.isNullOrEmpty(tableName)) {
			this.oriTableName = Strings.isNullOrEmpty(table.name()) ? domainClass.getSimpleName() : table.name();
		} else {
			this.oriTableName = tableName;
		}
		if (!isPartitionDateTable && !Strings.isNullOrEmpty(table.schema())) {
			log.info("CREATE TABLE [" + this.oriTableName + "] ...");
			this.createIfNotExists(null);
		}
	}

	//////////////////
	/// CRUD VIEW
	//////////////////
	private String _selectSQL;

	protected String getSelectSQL() {
		if (null == _selectSQL) {
			StringBuilder sb = new StringBuilder("SELECT ");
			boolean isFirst = true;
			for (Field f : domainClass.getFields()) {
				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(',');
					sb.append(' ');
				}
				String c = getSqlColumnValueName(f);
				if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
					sb.append('`');
					sb.append(c);
					sb.append('`');
				} else {
					sb.append(c);
				}
			}
			sb.append(" FROM ");
			_selectSQL = sb.toString();
		}
		return _selectSQL;
	}

	protected String getSqlColumnNickName(String fieldName) {
		try {
			return getSqlColumnNickName(domainClass.getField(fieldName));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e.getClass().getName() + ":" + e.getMessage());
		}
	}

	//////////////////
	/// CRUD VIEW
	//////////////////

	private CrudTableInfo _crudView;

	public CrudTableInfo getCrudView() {
		if (null != _crudView) {
			return _crudView;
		}
		_crudView = new CrudTableInfo();
		CrudTable cv = domainClass.getAnnotation(CrudTable.class);
		if (null != cv) {
			_crudView.idField = this.getIdColumnName();
			_crudView.title = cv.title();
			_crudView.width = cv.width();
			_crudView.height = cv.height();
			_crudView.createable = cv.createable();
			_crudView.createCheckNull = cv.createCheckNull();
			_crudView.updateable = cv.updateable();
			_crudView.updateCheckNull = cv.updateCheckNull();
			_crudView.exportable = cv.exportable();
			_crudView.partitionDateColumn = cv.partitionDateColumn();
		}
		if (Strings.isNullOrEmpty(_crudView.title)) {
			_crudView.title = domainClass.getSimpleName();
		}
		//
		_crudView.columns = new CrudColumnInfo[domainClass.getFields().length];
		int i = 0;
		for (Field f : domainClass.getFields()) {
			CrudColumnInfo v = new CrudColumnInfo();
			v.field = f.getName();
			v.fieldType = f.getType().getSimpleName();
			CrudColumn c = f.getAnnotation(CrudColumn.class);
			if (null != c) {
				v.title = c.title();
				v.header = c.header();
				v.sortable = c.sortable();
				v.filterable = c.filterable();
				v.width = c.width();
				v.height = c.height();
				v.createable = c.createable();
				v.required = c.required();
				v.updateable = c.updateable();
				v.readonly = c.readonly();
			}
			if (Strings.isNullOrEmpty(v.title)) {
				v.title = v.field;
			}
			_crudView.columns[i++] = v;
		}
		return _crudView;
	}

	//////////////////
	///
	//////////////////
	public List<T> findAllBySQL(String sql, Object... args) {
		return findAllBySQL(domainClass, sql, args);
	}

	public T findOneBySQL(String sql, Object... args) {
		return findOneBySQL(domainClass, sql, args);
	}

	public int execute(String sql, Object... args) {
		return jdbcTemplate.update(sql, args);
	}

	//////////////////
	///
	//////////////////
	public int countAllBySQL(String sql, Object... args) {
		log.debug("COUNT_ALL: " + sql);
		return jdbcTemplate.queryForObject(sql, args, Long.class).intValue();
	}

	public <E> List<E> findAllBySQL(final Class<E> domainClass, String sql, Object... args) {
		log.debug("FIND_ALL: " + sql);
		return jdbcTemplate.query(sql, args, (rs, i) -> {
			try {
				E domain = domainClass.newInstance();
				for (Field f : domainClass.getFields()) {
					switch (f.getType().getSimpleName()) {
					case ("String"):
						f.set(domain, rs.getString(getSqlColumnNickName(f)));
						continue;
					case ("Integer"):
						f.set(domain, rs.getInt(getSqlColumnNickName(f)));
						continue;
					case ("Long"):
						f.set(domain, rs.getLong(getSqlColumnNickName(f)));
						continue;
					case ("Date"):
						f.set(domain, rs.getTimestamp(getSqlColumnNickName(f)));
						continue;
					case ("Short"):
						f.set(domain, rs.getShort(getSqlColumnNickName(f)));
						continue;
					case ("Double"):
						f.set(domain, rs.getDouble(getSqlColumnNickName(f)));
						continue;
					case ("Float"):
						f.set(domain, rs.getFloat(getSqlColumnNickName(f)));
						continue;
					case ("Boolean"):
						f.set(domain, rs.getBoolean(getSqlColumnNickName(f)));
						continue;
					default:
						log.warn("BaseDao.findAll(): Cannot convert field from database for "
								+ domainClass.getSimpleName() + "." + f.getType().getSimpleName());
						continue;
					}
				}
				return domain;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	public <E> E findBasicTypeBySQL(Class<E> basicType, String sql, Object... args) {
		List<E> ls = findBasicTypesBySQL(basicType, sql, args);
		return (null == ls || ls.size() == 0) ? null : ls.get(0);
	}

	/**
	 * 返回 List<String>, List<Long> 等类型
	 * 
	 * @param domainClass
	 * @param sql
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> List<E> findBasicTypesBySQL(Class<E> basicType, String sql, Object... args) {
		switch (basicType.getSimpleName()) {
		case ("String"):
			return jdbcTemplate.query(sql, args, (r, i) -> (E) r.getString(1));
		case ("Integer"):
			return jdbcTemplate.query(sql, args, (r, i) -> (E) Integer.valueOf(r.getInt(1)));
		case ("Long"):
			return jdbcTemplate.query(sql, args, (r, i) -> (E) Long.valueOf(r.getLong(1)));
		case ("Date"):
			return jdbcTemplate.query(sql, args, (r, i) -> (E) r.getTimestamp(1));
		case ("Short"):
			return jdbcTemplate.query(sql, args, (r, i) -> (E) Short.valueOf(r.getShort(1)));
		case ("Double"):
			return jdbcTemplate.query(sql, args, (r, i) -> (E) Double.valueOf(r.getDouble(1)));
		case ("Float"):
			return jdbcTemplate.query(sql, args, (r, i) -> (E) Float.valueOf(r.getFloat(1)));
		case ("Boolean"):
			return jdbcTemplate.query(sql, args, (r, i) -> (E) Boolean.valueOf(r.getBoolean(1)));
		default:
			throw Exceptions.newInstance(
					"Dao.findBasicTypesBySQL(): Cannot convert field from database for " + domainClass.getSimpleName());
		}
	}

	public <E> E findOneBySQL(Class<E> domainClass, String sql, Object... args) {
		List<E> list = findAllBySQL(domainClass, sql, args);
		return list.size() > 0 ? list.get(0) : null;
	}

	//////////////////
	/// RETRIEVE LIST
	//////////////////
	public List<T> findAll(int page, int size) {
		return findAll(page, size, new DaoOrder[0]);
	}

	public List<T> findAll(int page, int size, DaoWhere... wheres) {
		return findAll(page, size, new DaoOrder[0], wheres);
	}

	public List<T> findAll(int page, int size, DaoOrder order, DaoWhere... wheres) {
		return findAll(page, size, new DaoOrder[] { order }, wheres);
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
		if (page <= 0) {
			page = 1;
		}
		if (size <= 0) {
			size = 1;
		}
		StringBuilder sb = new StringBuilder(getSelectSQL() + getTableName(wheres));
		if (null != wheres && wheres.length > 0) {
			sb.append(" WHERE ");
			boolean isAppend = false;
			for (DaoWhere dw : wheres) {
				if (isAppend) {
					sb.append(" AND ");
				} else {
					isAppend = true;
				}
				String c = getSqlColumnNickName(dw.column);
				if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
					sb.append('`');
					sb.append(c);
					sb.append('`');
				} else {
					sb.append(c);
				}
				sb.append(' ');
				sb.append(dw.operator.operator);
				sb.append(' ');
				sb.append('"');
				sb.append(dw.value);
				sb.append('"');
			}
		}
		if (null != orders && orders.length > 0) {
			sb.append(" ORDER BY ");
			boolean isAppend = false;
			for (DaoOrder i : orders) {
				if (isAppend) {
					sb.append(',');
					sb.append(' ');
				} else {
					isAppend = true;
				}
				String c = getSqlColumnNickName(i.column);
				if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
					sb.append('`');
					sb.append(c);
					sb.append('`');
				} else {
					sb.append(c);
				}
				sb.append(' ');
				sb.append(i.type.toString());
			}
		}
		sb.append(" LIMIT ");
		sb.append((page - 1) * size);
		sb.append(',');
		sb.append(' ');
		sb.append(size);
		return findAllBySQL(domainClass, sb.toString());
	}

	public int countAll() {
		return countAll(new DaoWhere[0]);
	}

	/**
	 * 
	 * @param wheres
	 * @return
	 */
	public int countAll(DaoWhere... wheres) {
		if (null == wheres || wheres.length == 0) {
			return countAllBySQL("SELECT COUNT(0) FROM " + getTableName(wheres));
		}
		StringBuilder sb = new StringBuilder("SELECT COUNT(0) FROM " + getTableName(wheres));
		sb.append(" WHERE ");
		boolean isAppend = false;
		for (DaoWhere dw : wheres) {
			if (isAppend) {
				sb.append(" AND ");
			} else {
				isAppend = true;
			}
			String c = getSqlColumnNickName(dw.column);
			if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
				sb.append('`');
				sb.append(c);
				sb.append('`');
			} else {
				sb.append(c);
			}
			sb.append(' ');
			sb.append(dw.operator.operator);
			sb.append(' ');
			sb.append('"');
			sb.append(dw.value);
			sb.append('"');
		}
		return countAllBySQL(sb.toString());
	}

	//////////////////
	/// RETRIEVE ONE
	//////////////////
	public T findOne(DaoWhere... wheres) {
		List<T> r = findAll(1, 1, new DaoOrder[0], wheres);
		return r.size() > 0 ? r.get(0) : null;
	}

	public T findOne(DaoOrder order, DaoWhere... wheres) {
		List<T> r = findAll(1, 1, new DaoOrder[] { order }, wheres);
		return r.size() > 0 ? r.get(0) : null;
	}

	public T findOne(DaoOrder[] orders, DaoWhere... wheres) {
		List<T> r = findAll(1, 1, orders, wheres);
		return r.size() > 0 ? r.get(0) : null;
	}

	/**
	 * 
	 * @param idValue
	 * @param partitionDate
	 * @return
	 */
	public T findOne(String idValue, DateTime partitionDate) {
		StringBuilder sb = new StringBuilder(getSelectSQL() + getTableName(partitionDate));
		sb.append(" WHERE ");
		String c = getIdColumnName();
		if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
			sb.append('`');
			sb.append(c);
			sb.append('`');
		} else {
			sb.append(c);
		}
		sb.append(' ');
		sb.append(Operators.Equal.operator);
		sb.append(' ');
		sb.append('"');
		sb.append(idValue);
		sb.append('"');
		sb.append(" LIMIT 1");
		List<T> r = findAllBySQL(domainClass, sb.toString());
		return r.size() > 0 ? r.get(0) : null;
	}

	public T findOne(Integer idValue, DateTime partitionDate) {
		return findOne(String.valueOf(idValue), partitionDate);
	}

	public T findOne(Long idValue, DateTime partitionDate) {
		return findOne(String.valueOf(idValue), partitionDate);
	}

	//////////////////
	///
	//////////////////
	/**
	 * 
	 * @param entity
	 * @return
	 */
	private T inplaceAndGet(String insertOrReplace, T entity) {
		try {
			final StringBuilder sb = new StringBuilder(insertOrReplace);
			sb.append(" INTO ");
			sb.append(getTableName(entity));
			sb.append(" (");
			boolean isAppend = false;
			for (Field f : domainClass.getFields()) {
				if (isIdGenerateIdentity() && f.getName().equals(getIdColumnName())) {
					continue;
				}
				if (isAppend) {
					sb.append(',');
					sb.append(' ');
				} else {
					isAppend = true;
				}
				String c = getSqlColumnNickName(f);
				if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
					sb.append('`');
					sb.append(c);
					sb.append('`');
				} else {
					sb.append(c);
				}
			}
			sb.append(") VALUES(");
			isAppend = false;
			for (Field f : domainClass.getFields()) {
				if (isIdGenerateIdentity() && f.getName().equals(getIdColumnName())) {
					continue;
				}
				if (isAppend) {
					sb.append(',');
					sb.append(' ');
				} else {
					isAppend = true;
				}
				sb.append(getSqlValue(f, entity));
			}
			sb.append(')');
			//
			if (isIdGenerateIdentity()) {
				KeyHolder keyHolder = new GeneratedKeyHolder();
				int c = jdbcTemplate.update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						return con.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
					}
				}, keyHolder);
				if (c <= 0) {
					return null;
				}
				return findOne(String.valueOf(keyHolder.getKey()), getPartitionDate(entity));
			}
			int c = jdbcTemplate.update(sb.toString());
			if (c <= 0) {
				return null;
			}
			return findOne(String.valueOf(domainClass.getField(getIdColumnName()).get(entity)),
					getPartitionDate(entity));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public T insertAndGet(T entity) {
		return inplaceAndGet("INSERT", entity);
	}

	public T replaceAndGet(T entity) {
		return inplaceAndGet("REPLACE", entity);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	private int inplace(String insertOrReplace, T[] entities) {
		try {
			final StringBuilder sb = new StringBuilder(insertOrReplace);
			sb.append(" INTO ");
			sb.append(getTableName(entities[0]));
			sb.append(" (");
			boolean isAppend = false;
			for (Field f : domainClass.getFields()) {
				if (isIdGenerateIdentity() && f.getName().equals(getIdColumnName())) {
					continue;
				}
				if (isAppend) {
					sb.append(',');
					sb.append(' ');
				} else {
					isAppend = true;
				}
				String c = getSqlColumnNickName(f);
				if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
					sb.append('`');
					sb.append(c);
					sb.append('`');
				} else {
					sb.append(c);
				}
			}
			sb.append(") VALUES");
			//
			for (T entity : entities) {
				sb.append('(');
				isAppend = false;
				for (Field f : domainClass.getFields()) {
					if (isIdGenerateIdentity() && f.getName().equals(getIdColumnName())) {
						continue;
					}
					if (isAppend) {
						sb.append(',');
						sb.append(' ');
					} else {
						isAppend = true;
					}
					sb.append(getSqlValue(f, entity));
				}
				sb.append("),");
			}
			//
			return jdbcTemplate.update(sb.substring(0, sb.length() - 1));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int insert(@SuppressWarnings("unchecked") T... entities) {
		return inplace("INSERT", entities);
	}

	public int replace(@SuppressWarnings("unchecked") T... entities) {
		return inplace("REPLACE", entities);
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
		try {
			final StringBuilder sb = new StringBuilder("UPDATE ");
			sb.append(getTableName(entity));
			sb.append(" SET ");
			boolean isAppend = false;
			for (Field f : domainClass.getFields()) {
				if (f.getName().equals(getIdColumnName())) {
					continue;
				}
				if (isAppend) {
					sb.append(',');
					sb.append(' ');
				} else {
					isAppend = true;
				}
				String c = getSqlColumnNickName(f);
				if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
					sb.append('`');
					sb.append(c);
					sb.append('`');
				} else {
					sb.append(c);
				}
				sb.append(' ');
				sb.append('=');
				sb.append(' ');
				sb.append(getSqlValue(f, entity));
			}
			sb.append(" WHERE ");
			String c = getIdColumnName();
			if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
				sb.append('`');
				sb.append(c);
				sb.append('`');
			} else {
				sb.append(c);
			}
			sb.append(' ');
			sb.append('=');
			sb.append(' ');
			sb.append('"');
			sb.append(idValue);
			sb.append('"');
			if (jdbcTemplate.update(sb.toString()) <= 0) {
				return null;
			}
			return findOne(String.valueOf(domainClass.getField(getIdColumnName()).get(entity)),
					getPartitionDate(entity));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public T updateAndGet(T entity) {
		try {
			return updateAndGet(entity, String.valueOf(domainClass.getField(getIdColumnName()).get(entity)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param entity
	 * @param idValue
	 * @return
	 */
	public int update(T entity, String idValue) {
		try {
			final StringBuilder sb = new StringBuilder("UPDATE ");
			sb.append(getTableName(entity));
			sb.append(" SET ");
			boolean isAppend = false;
			for (Field f : domainClass.getFields()) {
				if (f.getName().equals(getIdColumnName())) {
					continue;
				}
				if (isAppend) {
					sb.append(',');
					sb.append(' ');
				} else {
					isAppend = true;
				}
				String c = getSqlColumnNickName(f);
				if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
					sb.append('`');
					sb.append(c);
					sb.append('`');
				} else {
					sb.append(c);
				}
				sb.append(' ');
				sb.append('=');
				sb.append(' ');
				sb.append(getSqlValue(f, entity));
			}
			sb.append(" WHERE ");
			String c = getIdColumnName();
			if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
				sb.append('`');
				sb.append(c);
				sb.append('`');
			} else {
				sb.append(c);
			}
			sb.append(' ');
			sb.append('=');
			sb.append(' ');
			sb.append('"');
			sb.append(idValue);
			sb.append('"');
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int update(T entity) {
		try {
			return update(entity, String.valueOf(domainClass.getField(getIdColumnName()).get(entity)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
		StringBuilder sb = new StringBuilder("DELETE FROM ");
		sb.append(getTableName(wheres));
		if (null == wheres || wheres.length == 0) {
			throw new RuntimeException("Can't delete all by call deleteAll(null)");
		}
		sb.append(" WHERE ");
		boolean isAppend = false;
		for (DaoWhere dw : wheres) {
			if (isAppend) {
				sb.append(" AND ");
			} else {
				isAppend = true;
			}
			String c = getSqlColumnNickName(dw.column);
			if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
				sb.append('`');
				sb.append(c);
				sb.append('`');
			} else {
				sb.append(c);
			}
			sb.append(' ');
			sb.append(dw.operator.operator);
			sb.append(' ');
			sb.append('"');
			sb.append(dw.value);
			sb.append('"');
		}
		log.info("DELETE ALL: " + sb.toString());
		return jdbcTemplate.update(sb.toString());
	}

	/**
	 * 
	 * @param idValue
	 * @param partitionDate
	 * @return
	 */
	public int delete(String idValue, DateTime partitionDate) {
		StringBuilder sb = new StringBuilder("DELETE FROM ");
		sb.append(getTableName(partitionDate));
		sb.append(" WHERE ");
		String c = getIdColumnName();
		if (c.indexOf('`') < 0 && c.indexOf(' ') < 0 && c.indexOf('(') < 0) {
			sb.append('`');
			sb.append(c);
			sb.append('`');
		} else {
			sb.append(c);
		}
		sb.append(' ');
		sb.append(Operators.Equal.operator);
		sb.append(' ');
		sb.append('"');
		sb.append(idValue);
		sb.append('"');
		return jdbcTemplate.update(sb.toString());
	}

	public int delete(Integer idValue, DateTime partitionDate) {
		return delete(String.valueOf(idValue), partitionDate);
	}

	public int delete(Long idValue, DateTime partitionDate) {
		return delete(String.valueOf(idValue), partitionDate);
	}

	//////////////////
	///
	//////////////////
	public void loadCsv(String csvFilename, DateTime partitionDate) {
		boolean isLinuxOrWindows = Environment.NewLine.equals("\n");
		String sql = "LOAD DATA INFILE \"" + (isLinuxOrWindows ? csvFilename : csvFilename.substring(1))
				+ "\" INTO TABLE " + getTableName(partitionDate) + " character set utf8"
				+ " FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '" + Environment.NewLine + "';";
		log.debug("SQL(LOAD):" + sql.replace("\n", "\\n").replace("\r", "\\r"));
		jdbcTemplate.execute(sql);
	}

	public void dropIfExists(DateTime partitionDate) {
		String sql = "DROP TABLE IF EXISTS " + getTableName(partitionDate);
		log.info("SQL(DROP):" + sql);
		jdbcTemplate.execute(sql);
	}

	public void createIfNotExists(DateTime partitionDate) {
		Table table = domainClass.getAnnotation(Table.class);
		if (null == table || Strings.isNullOrEmpty(table.schema())) {
			throw Exceptions.newInstance(domainClass.getName() + "'s [Annotation]Table.schema is null or empty.");
		}
		StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		// sb.append('`');
		sb.append(getTableName(partitionDate));
		// sb.append('`');
		sb.append("(");
		sb.append(table.schema());
		sb.append(")ENGINE=MyISAM DEFAULT CHARSET=UTF8;");
		String sql = sb.toString();
		log.debug("SQL(CREATE):" + sql);
		jdbcTemplate.execute(sql);
	}

}
