package com.jspring.data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jspring.Environment;
import com.jspring.data.DaoWhere.Operators;
import com.jspring.date.DateTime;

public class CrudRepository<T> {

	public CrudRepository() {
	}

	public CrudRepository(SqlExecutor sqlExecutor, Class<T> entityClass) {
		this.sqlExecutor = sqlExecutor;
		log.info(">>> [ENTITY]" + entityClass.getName());
		_entityInfo = (MetaEntity<T>) MetaEntity.getMetaEntity(entityClass);
	}

	protected static final Logger log = LoggerFactory.getLogger(CrudRepository.class);

	//////////////////
	/// FIELDS
	//////////////////
	@Autowired
	private SqlExecutor sqlExecutor;

	protected SqlExecutor getSqlExecutor() {
		return sqlExecutor;
	}

	private MetaEntity<T> _entityInfo;

	@SuppressWarnings("unchecked")
	protected MetaEntity<T> getMetaEntity() {
		if (null == _entityInfo) {
			synchronized (this) {
				if (null == _entityInfo) {
					Type genType = getClass().getGenericSuperclass();
					Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
					Class<T> entityClass = (Class<T>) params[0];
					log.info(">>> [ENTITY]" + entityClass.getName());
					_entityInfo = (MetaEntity<T>) MetaEntity.getMetaEntity(entityClass);
				}
			}
		}
		return _entityInfo;
	}

	//////////////////
	/// METHODS
	//////////////////
	public List<T> findAllBySql(String sql, Object... args) {
		return sqlExecutor.queryEntities(getMetaEntity(), sql, args);
	}

	public T findOneBySql(String sql, Object... args) {
		return sqlExecutor.queryEntity(getMetaEntity(), sql, args);
	}

	//////////////////
	/// SELECT ALL
	//////////////////
	private String _selectSQL;

	protected String getSelectSQL() {
		if (null == _selectSQL) {
			StringBuilder sb = new StringBuilder("SELECT ");
			boolean isFirst = true;
			for (String column : getMetaEntity().getSqlColumnExpressions()) {
				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(',');
					sb.append(' ');
				}
				sb.append(column);
			}
			sb.append(" FROM ");
			_selectSQL = sb.toString();
		}
		return _selectSQL;
	}

	public List<T> findAll(int page, int size, DaoOrder[] orders, DaoWhere... wheres) {
		if (page <= 0) {
			page = 1;
		}
		if (size <= 0) {
			size = 1;
		}
		StringBuilder sb = new StringBuilder(getSelectSQL()//
				+ getMetaEntity().getSqlTableExpression());
		List<Object> args = new ArrayList<>();
		if (null != wheres && wheres.length > 0) {
			sb.append(" WHERE ");
			boolean isAppend = false;
			for (DaoWhere dw : wheres) {
				if (isAppend) {
					sb.append(" AND ");
				} else {
					isAppend = true;
				}
				String c = getMetaEntity().getMetaField(dw.column).getSqlColumnName();// getSqlColumnName(dw.column);
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
				sb.append('?');
				args.add(dw.value);
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
				String c = getMetaEntity().getMetaField(i.column).getSqlColumnName();// getSqlColumnName(i.column);
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
		if (args.size() > 0) {
			return findAllBySql(sb.toString(), args.toArray(new Object[0]));
		}
		return findAllBySql(sb.toString());
	}

	public List<T> findAll(int page, int size, DaoOrder order, DaoWhere... wheres) {
		return findAll(page, size, new DaoOrder[] { order }, wheres);
	}

	public List<T> findAll(int page, int size, DaoWhere... wheres) {
		return findAll(page, size, new DaoOrder[0], wheres);
	}

	public Long countAll(DaoWhere... wheres) {
		if (null == wheres || wheres.length == 0) {
			return getSqlExecutor().queryObject(getMetaEntity().getDatabase(), Long.class,
					"SELECT COUNT(0) FROM " + getMetaEntity().getSqlTableExpression());// getTableName(wheres));
		}
		StringBuilder sb = new StringBuilder("SELECT COUNT(0) FROM " + getMetaEntity().getSqlTableExpression());// getTableName(wheres));
		sb.append(" WHERE ");
		boolean isAppend = false;
		for (DaoWhere dw : wheres) {
			if (isAppend) {
				sb.append(" AND ");
			} else {
				isAppend = true;
			}
			String c = getMetaEntity().getMetaField(dw.column).getSqlColumnName();// getSqlColumnName(dw.column);
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
		return getSqlExecutor().queryObject(getMetaEntity().getDatabase(), Long.class, sb.toString());
	}

	//////////////////
	/// SELECT ONE
	//////////////////
	public T findOne(DaoOrder[] orders, DaoWhere... wheres) {
		StringBuilder sb = new StringBuilder(getSelectSQL()//
				+ getMetaEntity().getSqlTableExpression());
		List<Object> args = new ArrayList<>();
		if (null != wheres && wheres.length > 0) {
			sb.append(" WHERE ");
			boolean isAppend = false;
			for (DaoWhere dw : wheres) {
				if (isAppend) {
					sb.append(" AND ");
				} else {
					isAppend = true;
				}
				sb.append(getMetaEntity().getMetaField(dw.column).getSqlColumnName());
				sb.append(' ');
				sb.append(dw.operator.operator);
				sb.append(' ');
				sb.append('?');
				args.add(dw.value);
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
				sb.append(getMetaEntity().getMetaField(i.column).getSqlColumnName());
				sb.append(' ');
				sb.append(i.type.toString());
			}
		}
		sb.append(" LIMIT 1");
		if (args.size() > 0) {
			return findOneBySql(sb.toString(), args.toArray(new Object[0]));
		}
		return findOneBySql(sb.toString());
	}

	public T findOne(DaoOrder order, DaoWhere... wheres) {
		return findOne(new DaoOrder[] { order }, wheres);
	}

	public T findOne(DaoWhere... wheres) {
		return findOne(new DaoOrder[0], wheres);
	}

	public T findOneById(Object idValue) {
		StringBuilder sb = new StringBuilder(getSelectSQL()//
				+ getMetaEntity().getSqlTableExpression());
		sb.append(" WHERE ");
		sb.append(getMetaEntity().getIdColumn().getSqlColumnName());
		sb.append(' ');
		sb.append(Operators.Equal.operator);
		sb.append(' ');
		sb.append('?');
		sb.append(" LIMIT 1");
		return findOneBySql(sb.toString(), idValue);
	}

	//////////////////
	///
	//////////////////
	private int inplace(String insertOrReplace, T entity) {
		final StringBuilder sb = new StringBuilder(insertOrReplace);
		List<Object> args = new ArrayList<>();
		sb.append(" INTO ");
		sb.append(getMetaEntity().getSqlTableName());
		sb.append(" (");
		boolean isAppend = false;
		for (String column : getMetaEntity().getSqlEditableColumnNames()) {
			Object obj = getMetaEntity().getMetaField(column).getSqlColumnValue(entity);
			if (null == obj) {
				continue;
			}
			if (isAppend) {
				sb.append(',');
				sb.append(' ');
			} else {
				isAppend = true;
			}
			sb.append(column);
		}
		sb.append(") VALUES(");
		isAppend = false;
		for (String column : getMetaEntity().getSqlEditableColumnNames()) {
			Object obj = getMetaEntity().getMetaField(column).getSqlColumnValue(entity);
			if (null == obj) {
				continue;
			}
			if (isAppend) {
				sb.append(',');
				sb.append(' ');
			} else {
				isAppend = true;
			}
			sb.append('?');
			args.add(obj);
		}
		sb.append(')');
		//
		// if (isIdGenerateIdentity()) {
		// KeyHolder keyHolder = new GeneratedKeyHolder();
		// int c = jdbcTemplate.update(new PreparedStatementCreator() {
		// @Override
		// public PreparedStatement createPreparedStatement(Connection con)
		// throws SQLException {
		// return con.prepareStatement(sb.toString(),
		// Statement.RETURN_GENERATED_KEYS);
		// }
		// }, keyHolder);
		// if (c <= 0) {
		// return null;
		// }
		// return findOne(String.valueOf(keyHolder.getKey()),
		// getPartitionDate(entity));
		// }
		return getSqlExecutor().update(getMetaEntity().getDatabase(), sb.toString(), args.toArray(new Object[0]));
	}

	@SuppressWarnings("unchecked")
	public int insert(Object entity) {
		return inplace("INSERT", (T) entity);
	}

	@SuppressWarnings("unchecked")
	public int replace(Object entity) {
		return inplace("REPLACE", (T) entity);
	}

	//////////////////
	///
	//////////////////
	public int update(Object entity) {
		final StringBuilder sb = new StringBuilder("UPDATE ");
		sb.append(getMetaEntity().getSqlTableName());
		sb.append(" SET ");
		List<Object> args = new ArrayList<>();
		boolean isAppend = false;
		for (String column : getMetaEntity().getSqlEditableColumnNames()) {
			Object obj = getMetaEntity().getMetaField(column).getSqlColumnValue(entity);
			if (null == obj) {
				continue;
			}
			if (isAppend) {
				sb.append(',');
			} else {
				isAppend = true;
			}
			sb.append(column);
			sb.append('=');
			sb.append('?');
			args.add(obj);
		}
		sb.append(" WHERE ");
		sb.append(getMetaEntity().getIdColumn().getSqlColumnName());
		sb.append('=');
		sb.append('?');
		args.add(getMetaEntity().getIdColumn().getSqlColumnValue(entity));
		return getSqlExecutor().update(getMetaEntity().getDatabase(), sb.toString(), args.toArray(new Object[0]));
	}

	public int update(UnaryOperator<String> uop, String idValue) {
		Object entity = parseEntity(uop);
		final StringBuilder sb = new StringBuilder("UPDATE ");
		sb.append(getMetaEntity().getSqlTableName());
		sb.append(" SET ");
		List<Object> args = new ArrayList<>();
		boolean isAppend = false;
		for (String column : getMetaEntity().getSqlEditableColumnNames()) {
			Object obj = getMetaEntity().getMetaField(column).getSqlColumnValue(entity);
			if (null == obj) {
				continue;
			}
			if (isAppend) {
				sb.append(',');
			} else {
				isAppend = true;
			}
			sb.append(column);
			sb.append('=');
			sb.append('?');
			args.add(obj);
		}
		sb.append(" WHERE ");
		sb.append(getMetaEntity().getIdColumn().getSqlColumnName());
		sb.append('=');
		sb.append('?');
		args.add(idValue);
		return getSqlExecutor().update(getMetaEntity().getDatabase(), sb.toString(), args.toArray(new Object[0]));
	}

	//////////////////
	///
	//////////////////
	public int deleteAll(DaoWhere... wheres) {
		if (null == wheres || wheres.length == 0) {
			throw new RuntimeException("Can't delete all by call deleteAll(null)");
		}
		StringBuilder sb = new StringBuilder("DELETE FROM ");
		sb.append(getMetaEntity().getSqlTableName());
		sb.append(" WHERE ");
		List<Object> args = new ArrayList<>();
		boolean isAppend = false;
		for (DaoWhere dw : wheres) {
			if (isAppend) {
				sb.append(" AND ");
			} else {
				isAppend = true;
			}
			sb.append(getMetaEntity().getMetaField(dw.column).getSqlColumnName());
			sb.append(' ');
			sb.append(dw.operator.operator);
			sb.append(' ');
			sb.append('?');
			args.add(dw.value);
		}
		return getSqlExecutor().update(getMetaEntity().getDatabase(), sb.toString(), args.toArray(new Object[0]));
	}

	public int delete(Object idValue) {
		StringBuilder sb = new StringBuilder("DELETE FROM ");
		sb.append(getMetaEntity().getSqlTableName());
		sb.append(" WHERE ");
		sb.append(getMetaEntity().getIdColumn().getSqlColumnName());
		sb.append(Operators.Equal.operator);
		sb.append('?');
		return getSqlExecutor().update(getMetaEntity().getDatabase(), sb.toString(), idValue);
	}

	//////////////////
	///
	//////////////////
	public int loadCsv(String csvFilename, DateTime partitionDate) {
		boolean isLinuxOrWindows = Environment.NewLine.equals("\n");
		String sql = "LOAD DATA INFILE \""//
				+ (isLinuxOrWindows ? csvFilename : csvFilename.substring(1))//
				+ "\" INTO TABLE " + getMetaEntity().getSqlTableName() //
				+ " character set utf8"//
				+ " FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '"//
				+ Environment.NewLine + "';";
		return getSqlExecutor().update(getMetaEntity().getDatabase(), sql);
	}

	public int dropIfExists(DateTime partitionDate) {
		return getSqlExecutor().update(getMetaEntity().getDatabase(),
				"DROP TABLE IF EXISTS " + getMetaEntity().getSqlTableName());
	}

	public int createIfNotExists(DateTime partitionDate) {
		StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		sb.append(getMetaEntity().getSqlTableName());
		sb.append("(");
		sb.append(getMetaEntity().getSqlTableSchema());
		sb.append(")ENGINE=MyISAM DEFAULT CHARSET=UTF8;");
		return getSqlExecutor().update(getMetaEntity().getDatabase(), sb.toString());
	}

	//////////////////
	///
	//////////////////
	public T parseEntity(UnaryOperator<String> uop) {
		return MetaField.MAPPER(getMetaEntity(), uop);
	}

}