package com.jspring.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.collections.KeyValue;

@Component
public final class SqlExecutor {

	//////////////////
	/// STATIC
	//////////////////
	protected static final Logger log = LoggerFactory.getLogger(SqlExecutor.class);

	//////////////////
	/// FIELDS
	//////////////////
	@Autowired
	private Environment environment;

	private JdbcTemplate _jdbcTemplate = null;
	private List<KeyValue<String, JdbcTemplate>> _jdbcTemplates = new ArrayList<>();

	private JdbcTemplate getJdbcTemplate(String schema) {
		if (Strings.isNullOrEmpty(schema) || "spring".equals(schema)) {
			if (null == _jdbcTemplate) {
				synchronized (this) {
					if (null == _jdbcTemplate) {
						_jdbcTemplate = buildJdbcTemplate("spring");
					}
					return _jdbcTemplate;
				}
			}
			return _jdbcTemplate;
		}
		for (KeyValue<String, JdbcTemplate> kv : _jdbcTemplates) {
			if (kv.key.equals(schema)) {
				return kv.value;
			}
		}
		synchronized (this) {
			for (KeyValue<String, JdbcTemplate> kv : _jdbcTemplates) {
				if (kv.key.equals(schema)) {
					return kv.value;
				}
			}
			JdbcTemplate jt = buildJdbcTemplate(schema);
			_jdbcTemplates.add(new KeyValue<>(schema, jt));
			return jt;
		}
	}

	private JdbcTemplate buildJdbcTemplate(String schema) {
		String url = environment.getProperty(schema + ".datasource.url");
		if (Strings.isNullOrEmpty(url)) {
			throw Exceptions.newNullArgumentException("[Properties]" + schema + ".datasource.url");
		}
		String username = environment.getProperty(schema + ".datasource.username");
		String password = environment.getProperty(schema + ".datasource.password");
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return new JdbcTemplate(dataSource);
	}

	//////////////////
	/// PARSER
	//////////////////

	@SuppressWarnings("unchecked")
	protected <T> RowMapper<T> getRowMapper(JColumnValue[] columns) {
		return (rs, i) -> {
			try {
				Object domain = columns[0].tableValue.domain.newInstance();
				p: for (JColumnValue column : columns) {
					switch (column.type) {
					case String:
						column.field.set(domain, rs.getString(column.getColumnName()));
						continue p;
					case Integer:
						column.field.set(domain, rs.getInt(column.getColumnName()));
						continue p;
					case Long:
						column.field.set(domain, rs.getLong(column.getColumnName()));
						continue p;
					case Short:
						column.field.set(domain, rs.getShort(column.getColumnName()));
						continue p;
					case Double:
						column.field.set(domain, rs.getDouble(column.getColumnName()));
						continue p;
					case Float:
						column.field.set(domain, rs.getFloat(column.getColumnName()));
						continue p;
					case Boolean:
						column.field.set(domain, rs.getBoolean(column.getColumnName()));
						continue p;
					case Date:
						column.field.set(domain, rs.getTimestamp(column.getColumnName()));
						continue p;
					case DateTime:
						column.field.set(domain, rs.getTimestamp(column.getColumnName()));
						continue p;
					default:
						continue p;
					}
				}
				return (T) domain;
			} catch (Exception e) {
				throw Exceptions.newInstance(e);
			}
		};
	}

	protected <T> RowMapper<T> getRowMapper(Class<T> pojoClass) {
		return (rs, i) -> {
			try {
				T domain = pojoClass.newInstance();
				p: for (Field f : pojoClass.getFields()) {
					switch (JColumnTypes.of(f)) {
					case String:
						f.set(domain, rs.getString(f.getName()));
						continue p;
					case Integer:
						f.set(domain, rs.getInt(f.getName()));
						continue p;
					case Long:
						f.set(domain, rs.getLong(f.getName()));
						continue p;
					case Short:
						f.set(domain, rs.getShort(f.getName()));
						continue p;
					case Double:
						f.set(domain, rs.getDouble(f.getName()));
						continue p;
					case Float:
						f.set(domain, rs.getFloat(f.getName()));
						continue p;
					case Boolean:
						f.set(domain, rs.getBoolean(f.getName()));
						continue p;
					case Date:
						f.set(domain, rs.getTimestamp(f.getName()));
						continue p;
					case DateTime:
						f.set(domain, rs.getTimestamp(f.getName()));
						continue p;
					default:
						continue p;
					}
				}
				return domain;
			} catch (Exception e) {
				throw Exceptions.newInstance(e);
			}
		};
	}

//	@SuppressWarnings("unchecked")
//	public <T> T parseEntity(UnaryOperator<String> stringMap, JColumnValue[] columns) {
//		try {
//			Object domain = columns[0].tableData.domain.newInstance();
//			p: for (JColumnValue column : columns) {
//				String value = stringMap.apply(column.getFieldName());
//				if (Strings.isNullOrEmpty(value)) {
//					continue;
//				}
//				switch (column.type) {
//				case String:
//					column.field.set(domain, value);
//					continue p;
//				case Integer:
//					column.field.set(domain, Strings.valueOfInt(value));
//					continue p;
//				case Long:
//					column.field.set(domain, Strings.valueOfLong(value));
//					continue p;
//				case Short:
//					column.field.set(domain, Strings.valueOfShort(value));
//					continue p;
//				case Double:
//					column.field.set(domain, Strings.valueOfDouble(value));
//					continue p;
//				case Float:
//					column.field.set(domain, Strings.valueOfFloat(value));
//					continue p;
//				case Boolean:
//					column.field.set(domain, Strings.valueOfBool(value));
//					continue p;
//				case Date:
//					column.field.set(domain, Strings.valueOfDateTime(value).getLocalDate());
//					continue p;
//				case DateTime:
//					column.field.set(domain, Strings.valueOfDateTime(value).getLocalDate());
//					continue p;
//				default:
//					continue p;
//				}
//			}
//			return (T) domain;
//		} catch (Exception e) {
//			throw Exceptions.newInstance(e);
//		}
//	}

	//////////////////
	/// EXECUTE
	//////////////////

	/**
	 * 获取实体列表
	 * 
	 */
	public <E> List<E> queryEntities(String schema, JColumnValue[] columns, String sql, Object... args) {
		log.debug("List<[Entity]" + columns[0].tableValue.domain.getSimpleName() + ">: " + sql);
		return getJdbcTemplate(schema).query(sql, args, getRowMapper(columns));
	}

	/**
	 * 获取首个实体
	 */
	public <E> E queryEntity(String schema, JColumnValue[] columns, String sql, Object... args) {
		log.debug("[Entity]" + columns[0].tableValue.domain.getSimpleName() + ": " + sql);
		try {
			return getJdbcTemplate(schema).queryForObject(sql, args, getRowMapper(columns));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 获取实体快照列表，可用于统计查询等
	 * 
	 * @param pojoClass
	 *            不支持 @Table @Column 等
	 */
	public <J> List<J> queryPojos(String schema, Class<J> pojoClass, String sql, Object... args) {
		log.debug("List<[Pojo]" + pojoClass.getSimpleName() + ">: " + sql);
		return getJdbcTemplate(schema).query(sql, args, getRowMapper(pojoClass));
	}

	/**
	 * 获取实体快照，可用于统计查询等
	 * 
	 * @param pojoClass
	 *            不支持 @Table @Column 等
	 */
	public <J> J queryPojo(String schema, Class<J> pojoClass, String sql, Object... args) {
		log.debug("[Pojo]" + pojoClass.getSimpleName() + ": " + sql);
		try {
			return getJdbcTemplate(schema).queryForObject(sql, args, getRowMapper(pojoClass));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 获取基础类型的列表
	 * 
	 * @param primitiveType
	 */
	@SuppressWarnings("unchecked")
	public <P> List<P> queryPrimitives(String schema, Class<P> primitiveType, String sql, Object... args) {
		log.debug("List<[Primitive]" + primitiveType.getSimpleName() + ">: " + sql);
		switch (primitiveType.getSimpleName()) {
		case ("String"):
			return getJdbcTemplate(schema).query(sql, args, (r, i) -> (P) r.getString(1));
		case ("Integer"):
			return getJdbcTemplate(schema).query(sql, args, (r, i) -> (P) Integer.valueOf(r.getInt(1)));
		case ("Long"):
			return getJdbcTemplate(schema).query(sql, args, (r, i) -> (P) Long.valueOf(r.getLong(1)));
		case ("Short"):
			return getJdbcTemplate(schema).query(sql, args, (r, i) -> (P) Short.valueOf(r.getShort(1)));
		case ("Double"):
			return getJdbcTemplate(schema).query(sql, args, (r, i) -> (P) Double.valueOf(r.getDouble(1)));
		case ("Float"):
			return getJdbcTemplate(schema).query(sql, args, (r, i) -> (P) Float.valueOf(r.getFloat(1)));
		case ("Boolean"):
			return getJdbcTemplate(schema).query(sql, args, (r, i) -> (P) Boolean.valueOf(r.getBoolean(1)));
		case ("Date"):
			return getJdbcTemplate(schema).query(sql, args, (r, i) -> (P) r.getTimestamp(1));
		default:
			throw Exceptions.newInstance("Illegale [primitiveType]" + primitiveType.getSimpleName());
		}
	}

	/**
	 * 获取基础类型的值
	 * 
	 * @param primitiveType
	 */
	@SuppressWarnings("unchecked")
	public <P> P queryPrimitive(String schema, Class<P> primitiveType, String sql, Object... args) {
		log.debug("[Primitive]" + primitiveType.getSimpleName() + ": " + sql);
		try {
			switch (primitiveType.getSimpleName()) {
			case ("String"):
				return (P) getJdbcTemplate(schema).queryForObject(sql, args, String.class);
			case ("Integer"):
				return (P) getJdbcTemplate(schema).queryForObject(sql, args, Integer.class);
			case ("Long"):
				return (P) getJdbcTemplate(schema).queryForObject(sql, args, Long.class);
			case ("Short"):
				return (P) getJdbcTemplate(schema).queryForObject(sql, args, Short.class);
			case ("Double"):
				return (P) getJdbcTemplate(schema).queryForObject(sql, args, Double.class);
			case ("Float"):
				return (P) getJdbcTemplate(schema).queryForObject(sql, args, Float.class);
			case ("Boolean"):
				return (P) getJdbcTemplate(schema).queryForObject(sql, args, Boolean.class);
			case ("Date"):
				return (P) getJdbcTemplate(schema).queryForObject(sql, args, Date.class);
			default:
				throw Exceptions.newInstance("Illegale [primitiveType]" + primitiveType.getSimpleName());
			}
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 执行并返回影响行数
	 * 
	 */
	public int executeNoneQuery(String schema, String sql, Object... args) {
		log.debug("executeNoneQuery: " + sql.replaceAll("\n", "\\n").replaceAll("\r", "\\r"));
		return getJdbcTemplate(schema).update(sql, args);
	}

}