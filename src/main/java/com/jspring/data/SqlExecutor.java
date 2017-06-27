package com.jspring.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.collections.KeyValue;

@Component
public final class SqlExecutor {

	//////////////////
	/// STATIC FIELDS
	//////////////////
	private static final Logger log = LoggerFactory.getLogger(SqlExecutor.class);

	//////////////////
	/// STATIC METHODS
	//////////////////
	/**
	 * 获取实体列表
	 * 
	 */
	private static <T> List<T> queryEntities(JdbcTemplate jdbcTemplate, MetaEntity<T> metaEntity, String sql,
			Object... args) {
		log.info(">>> [SQL]queryEntities<" + metaEntity.getEntityClass().getSimpleName() + ">: " + sql);
		return jdbcTemplate.query(sql, args, MetaField.ROW_MAPPER(metaEntity));
	}

	/**
	 * 获取首个实体
	 */
	private static <T> T queryEntity(JdbcTemplate jdbcTemplate, MetaEntity<T> metaEntity, String sql, Object... args) {
		log.info(">>> [SQL]queryEntity<" + metaEntity.getEntityClass().getSimpleName() + ">: " + sql);
		return jdbcTemplate.queryForObject(sql, args, MetaField.ROW_MAPPER(metaEntity));
	}

	/**
	 * 获取实体快照，可用于统计查询等
	 * 
	 * @param pojoClass
	 *            不支持 @Table @Column 等
	 */
	private static <T> List<T> queryPojos(JdbcTemplate jdbcTemplate, Class<T> pojoClass, String sql, Object... args) {
		log.info(">>> [SQL]queryPojos<" + pojoClass.getSimpleName() + ">: " + sql);
		return jdbcTemplate.query(sql, args, MetaField.ROW_MAPPER(pojoClass));
	}

	/**
	 * 获取实体快照，可用于统计查询等
	 * 
	 * @param pojoClass
	 *            不支持 @Table @Column 等
	 */
	private static <T> T queryPojo(JdbcTemplate jdbcTemplate, Class<T> pojoClass, String sql, Object... args) {
		log.info(">>> [SQL]queryPojo<" + pojoClass.getSimpleName() + ">: " + sql);
		return jdbcTemplate.queryForObject(sql, args, MetaField.ROW_MAPPER(pojoClass));
	}

	/**
	 * 获取表达式值的列表
	 * 
	 * @param sql
	 * @param basicType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <B> List<B> queryObjects(JdbcTemplate jdbcTemplate, Class<B> basicType, String sql, Object... args) {
		log.info(">>> [SQL]queryObjects<" + basicType.getSimpleName() + ">: " + sql);
		switch (basicType.getSimpleName()) {
		case ("String"):
			return jdbcTemplate.query(sql, args, (r, i) -> (B) r.getString(1));
		case ("Integer"):
			return jdbcTemplate.query(sql, args, (r, i) -> (B) Integer.valueOf(r.getInt(1)));
		case ("Long"):
			return jdbcTemplate.query(sql, args, (r, i) -> (B) Long.valueOf(r.getLong(1)));
		case ("Date"):
			return jdbcTemplate.query(sql, args, (r, i) -> (B) r.getTimestamp(1));
		case ("Short"):
			return jdbcTemplate.query(sql, args, (r, i) -> (B) Short.valueOf(r.getShort(1)));
		case ("Double"):
			return jdbcTemplate.query(sql, args, (r, i) -> (B) Double.valueOf(r.getDouble(1)));
		case ("Float"):
			return jdbcTemplate.query(sql, args, (r, i) -> (B) Float.valueOf(r.getFloat(1)));
		case ("Boolean"):
			return jdbcTemplate.query(sql, args, (r, i) -> (B) Boolean.valueOf(r.getBoolean(1)));
		default:
			throw Exceptions.newInstance("executeObjects(): Illegale BasicType " + basicType.getSimpleName());
		}
	}

	/**
	 * 获取首个表达式值
	 * 
	 * @param sql
	 * @param basicType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <B> B queryObject(JdbcTemplate jdbcTemplate, Class<B> basicType, String sql, Object... args) {
		log.info(">>> [SQL]queryObject<" + basicType.getSimpleName() + ">: " + sql);
		switch (basicType.getSimpleName()) {
		case ("String"):
			return (B) jdbcTemplate.queryForObject(sql, args, String.class);
		case ("Integer"):
			return (B) jdbcTemplate.queryForObject(sql, args, Integer.class);
		case ("Long"):
			return (B) jdbcTemplate.queryForObject(sql, args, Long.class);
		case ("Date"):
			return (B) jdbcTemplate.queryForObject(sql, args, Date.class);
		case ("Short"):
			return (B) jdbcTemplate.queryForObject(sql, args, Short.class);
		case ("Double"):
			return (B) jdbcTemplate.queryForObject(sql, args, Double.class);
		case ("Float"):
			return (B) jdbcTemplate.queryForObject(sql, args, Float.class);
		case ("Boolean"):
			return (B) jdbcTemplate.queryForObject(sql, args, Boolean.class);
		default:
			throw Exceptions.newInstance("executeObject(): Illegale BasicType " + basicType.getSimpleName());
		}
	}

	/**
	 * 执行返回影响行数
	 * 
	 * @param sql
	 */
	private static int update(JdbcTemplate jdbcTemplate, String sql, Object... args) {
		log.info(">>> [SQL]update: " + sql);
		return jdbcTemplate.update(sql, args);
	}

	//////////////////
	/// FIELDS
	//////////////////
	@Autowired
	private Environment environment;

	private JdbcTemplate jdbcTemplate = null;
	private List<KeyValue<String, JdbcTemplate>> jdbcTemplates = new ArrayList<>();

	private JdbcTemplate getJdbcTemplate(String databaseName) {
		if (Strings.isNullOrEmpty(databaseName) || "spring".equals(databaseName)) {
			if (null == jdbcTemplate) {
				synchronized (this) {
					if (null == jdbcTemplate) {
						jdbcTemplate = buildJdbcTemplate("spring");
					}
					return jdbcTemplate;
				}
			}
			return jdbcTemplate;
		}
		for (KeyValue<String, JdbcTemplate> kv : jdbcTemplates) {
			if (kv.key.equals(databaseName)) {
				return kv.value;
			}
		}
		synchronized (this) {
			for (KeyValue<String, JdbcTemplate> kv : jdbcTemplates) {
				if (kv.key.equals(databaseName)) {
					return kv.value;
				}
			}
			JdbcTemplate jt = buildJdbcTemplate(databaseName);
			jdbcTemplates.add(new KeyValue<>(databaseName, jt));
			return jt;
		}
	}

	private JdbcTemplate buildJdbcTemplate(String databaseName) {
		String url = environment.getProperty(databaseName + ".datasource.url");
		if (Strings.isNullOrEmpty(url)) {
			throw Exceptions.newNullArgumentException("[Properties]" + databaseName + ".datasource.url");
		}
		String username = environment.getProperty(databaseName + ".datasource.username");
		String password = environment.getProperty(databaseName + ".datasource.password");
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return new JdbcTemplate(dataSource);
	}

	//////////////////
	/// METHODS
	//////////////////
	public <T> List<T> queryEntities(MetaEntity<T> entityInfo, String sql, Object... args) {
		return queryEntities(getJdbcTemplate(entityInfo.getDatabase()), entityInfo, sql, args);
	}

	public <T> T queryEntity(MetaEntity<T> entityInfo, String sql, Object... args) {
		return queryEntity(getJdbcTemplate(entityInfo.getDatabase()), entityInfo, sql, args);
	}

	public <T> List<T> queryPojos(String dabaseName, Class<T> pojoClass, String sql, Object... args) {
		return queryPojos(getJdbcTemplate(dabaseName), pojoClass, sql, args);
	}

	public <T> T queryPojo(String dabaseName, Class<T> pojoClass, String sql, Object... args) {
		return queryPojo(getJdbcTemplate(dabaseName), pojoClass, sql, args);
	}

	public <B> List<B> queryObjects(String dabaseName, Class<B> basicType, String sql, Object... args) {
		return queryObjects(getJdbcTemplate(dabaseName), basicType, sql, args);
	}

	public <B> B queryObject(String dabaseName, Class<B> basicType, String sql, Object... args) {
		return queryObject(getJdbcTemplate(dabaseName), basicType, sql, args);
	}

	public int update(String dabaseName, String sql, Object... args) {
		return update(getJdbcTemplate(dabaseName), sql, args);
	}

}