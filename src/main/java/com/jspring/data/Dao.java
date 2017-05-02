package com.jspring.data;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.jspring.Environment;
import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.data.DaoWhere.Operators;
import com.jspring.date.DateTime;

public class Dao<T> {

	private static final Logger log = LoggerFactory.getLogger(Dao.class);

	protected static String getColumnSelectName(Field f) {
		Column column = f.getAnnotation(Column.class);
		if (null == column) {
			return f.getName();
		}
		if (!Strings.isNullOrEmpty(column.name())) {
			return column.name();
		}
		return f.getName();
	}

	protected static String getColumnName(Field f) {
		Column column = f.getAnnotation(Column.class);
		if (null == column) {
			return f.getName();
		}
		if (!Strings.isNullOrEmpty(column.name()) && column.name().indexOf(' ') < 0) {
			return column.name();
		}
		return f.getName();
	}

	//////////////////
	///
	//////////////////
	protected final JdbcTemplate jdbcTemplate;
	protected final Class<T> domainClass;

	//////////////////
	///
	//////////////////
	protected String _oriTableName = null;
	protected Field _partitionDateField = null;

	protected String getTableName(DaoWhere[] wheres) {
		if (null == _oriTableName) {
			Table table = domainClass.getAnnotation(Table.class);
			_oriTableName = (null == table || Strings.isNullOrEmpty(table.name())) ? domainClass.getSimpleName()
					: table.name();
		}
		if (Strings.isNullOrEmpty(getCrudView().partitionDateColumn)) {
			return _oriTableName;
		}
		for (DaoWhere w : wheres) {
			if (w.column.equals(getCrudView().partitionDateColumn)) {
				return _oriTableName + "_" + DateTime.valueOf(w.value).toShortDateString();
			}
		}
		throw Exceptions
				.newInstance("[PartitionDateColumn]" + getCrudView().partitionDateColumn + " cannot be null or empty.");
	}

	protected String getTableName(T entity)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (null == _oriTableName) {
			Table table = domainClass.getAnnotation(Table.class);
			_oriTableName = (null == table || Strings.isNullOrEmpty(table.name())) ? domainClass.getSimpleName()
					: table.name();
		}
		if (Strings.isNullOrEmpty(getCrudView().partitionDateColumn)) {
			return _oriTableName;
		}
		if (null == _partitionDateField) {
			_partitionDateField = domainClass.getField(getCrudView().partitionDateColumn);
		}
		DateTime partitionDate = (DateTime) _partitionDateField.get(entity);
		if (null == partitionDate) {
			throw Exceptions.newNullArgumentException("entity." + getCrudView().partitionDateColumn);
		}
		return _oriTableName + "_" + partitionDate.toShortDateString();
	}

	protected String getTableName(DateTime partitionDate) {
		if (null == _oriTableName) {
			Table table = domainClass.getAnnotation(Table.class);
			_oriTableName = (null == table || Strings.isNullOrEmpty(table.name())) ? domainClass.getSimpleName()
					: table.name();
		}
		if (Strings.isNullOrEmpty(getCrudView().partitionDateColumn)) {
			return _oriTableName;
		}
		if (null == partitionDate) {
			throw Exceptions.newNullArgumentException("partitionDate");
		}
		return _oriTableName + "_" + partitionDate.toShortDateString();
	}

	public DateTime getPartitionDate(T entity)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (Strings.isNullOrEmpty(getCrudView().partitionDateColumn)) {
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
		_idColumnName = getColumnName(getIdColumn());
		return _idColumnName;
	}

	private boolean _isIdGenerateIdentityInit = false;
	private boolean _isIdGenerateIdentity = false;

	public boolean isIdGenerateIdentity() {
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
	public Dao(JdbcTemplate jdbcTemplate, Class<T> domainClass) {
		this.jdbcTemplate = jdbcTemplate;
		this.domainClass = domainClass;
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
				sb.append(getColumnSelectName(f));
			}
			sb.append(" FROM ");
			_selectSQL = sb.toString();
		}
		return _selectSQL;
	}

	protected String getColumnName(String fieldName) {
		try {
			return getColumnName(domainClass.getField(fieldName));
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
				v.filter = c.filter().shortName;
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
		return findAll(domainClass, sql, args);
	}

	public T findOneBySQL(String sql, Object... args) {
		return findOne(domainClass, sql, args);
	}

	public int execute(String sql, Object... args) {
		return jdbcTemplate.update(sql, args);
	}

	//////////////////
	///
	//////////////////
	public int countAll(String sql, Object... args) {
		log.debug("COUNT_ALL: " + sql);
		return jdbcTemplate.queryForObject(sql, args, Long.class).intValue();
	}

	protected String convert2SQL(Field f, T entity) throws IllegalArgumentException, IllegalAccessException {
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
			default:
				throw new RuntimeException("BaseDao.convert2SQL(): Cannot convert field to SQL object for "
						+ domainClass.getSimpleName() + "." + f.getType().getSimpleName());
			}
		}
		if (f.getType().getSimpleName().equals("Date")) {
			return '"' + (new DateTime((Date) obj).toString()) + '"';
		}
		return '"' + String.valueOf(obj) + '"';
	}

	public <E> List<E> findAll(final Class<E> domainClass, String sql, Object... args) {
		log.debug("FIND_ALL: " + sql);
		return jdbcTemplate.query(sql, args, new RowMapper<E>() {
			@Override
			public E mapRow(ResultSet rs, int rowNum) throws SQLException {
				try {
					E domain = domainClass.newInstance();
					for (Field f : domainClass.getFields()) {
						switch (f.getType().getSimpleName()) {
						case ("String"):
							f.set(domain, rs.getString(getColumnName(f)));
							continue;
						case ("Integer"):
							f.set(domain, rs.getInt(getColumnName(f)));
							continue;
						case ("Long"):
							f.set(domain, rs.getLong(getColumnName(f)));
							continue;
						case ("Short"):
							f.set(domain, rs.getShort(getColumnName(f)));
							continue;
						case ("Double"):
							f.set(domain, rs.getDouble(getColumnName(f)));
							continue;
						case ("Date"):
							f.set(domain, rs.getTimestamp(getColumnName(f)));
							continue;
						case ("Float"):
							f.set(domain, rs.getFloat(getColumnName(f)));
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
			}
		});
	}

	public <E> E findOne(Class<E> domainClass, String sql, Object... args) {
		List<E> list = findAll(domainClass, sql, args);
		return list.size() > 0 ? list.get(0) : null;
	}

	//////////////////
	///
	//////////////////
	public List<T> findAll(int page, int size) {
		return findAll(page, size, null, null);
	}

	public List<T> findAll(int page, int size, DaoWhere... wheres) {
		return findAll(page, size, wheres, null);
	}

	public List<T> findAll(int page, int size, DaoWhere[] wheres, DaoOrder[] orders) {
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
				sb.append(getColumnName(dw.column));
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
				sb.append(getColumnName(i.column));
				sb.append(' ');
				sb.append(i.type.toString());
			}
		}
		sb.append(" LIMIT ");
		sb.append((page - 1) * size);
		sb.append(',');
		sb.append(' ');
		sb.append(size);
		return findAll(domainClass, sb.toString());
	}

	public int countAll(DaoWhere[] wheres) {
		if (null == wheres || wheres.length == 0) {
			return countAll("SELECT COUNT(0) FROM " + getTableName(wheres));
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
			sb.append(getColumnName(dw.column));
			sb.append(' ');
			sb.append(dw.operator.operator);
			sb.append(' ');
			sb.append('"');
			sb.append(dw.value);
			sb.append('"');
		}
		return countAll(sb.toString());
	}

	//////////////////
	///
	//////////////////
	public T findOne(DaoWhere[] wheres, DaoOrder[] orders) {
		StringBuilder sb = new StringBuilder(getSelectSQL() + getTableName(wheres));
		if (null == wheres || wheres.length == 0) {
			throw new RuntimeException("Can't find one by call findOne(null)");
		}
		if (null != wheres && wheres.length > 0) {
			sb.append(" WHERE ");
			boolean isAppend = false;
			for (DaoWhere dw : wheres) {
				if (isAppend) {
					sb.append(" AND ");
				} else {
					isAppend = true;
				}
				sb.append(getColumnName(dw.column));
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
			for (DaoOrder o : orders) {
				if (isAppend) {
					sb.append(", ");
				} else {
					isAppend = true;
				}
				sb.append(o.column);
				sb.append(' ');
				sb.append(o.type.toString());
			}
		}
		sb.append(" LIMIT 1");
		List<T> r = findAll(domainClass, sb.toString());
		return r.size() > 0 ? r.get(0) : null;
	}

	public T findOne(DaoWhere... wheres) {
		return findOne(wheres, null);
	}

	public T findOne(String idValue, DateTime partitionDate) {
		StringBuilder sb = new StringBuilder(getSelectSQL() + getTableName(partitionDate));
		sb.append(" WHERE ");
		sb.append(getIdColumnName());
		sb.append(' ');
		sb.append(Operators.Equal.operator);
		sb.append(' ');
		sb.append('"');
		sb.append(idValue);
		sb.append('"');
		sb.append(" LIMIT 1");
		List<T> r = findAll(domainClass, sb.toString());
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
	public T insertAndGet(T entity) {
		try {
			final StringBuilder sb = new StringBuilder("INSERT INTO ");
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
				sb.append(getColumnName(f));
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
				sb.append(convert2SQL(f, entity));
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

	public int insert(T entity) {
		try {
			final StringBuilder sb = new StringBuilder("INSERT INTO ");
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
				sb.append(getColumnName(f));
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
				sb.append(convert2SQL(f, entity));
			}
			sb.append(')');
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//////////////////
	///
	//////////////////
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
				sb.append(getColumnName(f));
				sb.append(' ');
				sb.append('=');
				sb.append(' ');
				sb.append(convert2SQL(f, entity));
			}
			sb.append(" WHERE ");
			sb.append(getIdColumnName());
			sb.append(' ');
			sb.append('=');
			sb.append(' ');
			sb.append('"');
			sb.append(idValue);
			sb.append('"');
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

	public T updateAndGet(T entity) {
		try {
			return updateAndGet(entity, String.valueOf(domainClass.getField(getIdColumnName()).get(entity)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

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
				sb.append(getColumnName(f));
				sb.append(' ');
				sb.append('=');
				sb.append(' ');
				sb.append(convert2SQL(f, entity));
			}
			sb.append(" WHERE ");
			sb.append(getIdColumnName());
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
			sb.append(getColumnName(dw.column));
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

	public int delete(String idValue, DateTime partitionDate) {
		StringBuilder sb = new StringBuilder("DELETE FROM ");
		sb.append(getTableName(partitionDate));
		sb.append(" WHERE ");
		sb.append(getIdColumnName());
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
				+ "\" INTO TABLE `" + getTableName(partitionDate) + "` character set utf8"
				+ " FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '" + Environment.NewLine + "';";
		log.info("SQL(LOAD):" + sql.replace("\n", "\\n").replace("\r", "\\r"));
		jdbcTemplate.execute(sql);
	}

	public void dropTable(DateTime partitionDate) {
		jdbcTemplate.execute("DROP TABLE " + getTableName(partitionDate));
	}

	public void createIfNotExist(String bodySQL, DateTime partitionDate) {
		StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		sb.append('`');
		sb.append(getTableName(partitionDate));
		sb.append('`');
		sb.append("(");
		sb.append(bodySQL);
		sb.append(")ENGINE=MyISAM DEFAULT CHARSET=UTF8;");
		String sql = sb.toString();
		log.info("SQL(CREATE):" + sql);
		jdbcTemplate.execute(sql);
	}

}
