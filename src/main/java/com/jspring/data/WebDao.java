package com.jspring.data;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.jspring.Strings;

public class WebDao<T> extends Dao<T> {

	public WebDao(JdbcTemplate jdbcTemplate, Class<T> domainClass) {
		super(jdbcTemplate, domainClass);
	}

	private static final Logger log = LoggerFactory.getLogger(WebDao.class);

	public static <E> E convertFrom(Class<E> domainClass, HttpServletRequest request) {
		try {
			E domain = domainClass.newInstance();
			for (Field f : domainClass.getFields()) {
				String cn = getColumnName(f);
				String value = request.getParameter(cn);
				if (Strings.isNullOrEmpty(value)) {
					Column cl = f.getAnnotation(Column.class);
					if (null == cl || cl.nullable()) {// Nullable
						continue;
					}
				}
				switch (f.getType().getSimpleName()) {
				case ("String"):
					f.set(domain, Strings.isNullOrEmpty(value) ? "" : value);
					continue;
				case ("Integer"):
					f.set(domain, Strings.isNullOrEmpty(value) ? 0 : Strings.valueOfInt(value));
					continue;
				case ("Long"):
					f.set(domain, Strings.isNullOrEmpty(value) ? 0L : Strings.valueOfLong(value));
					continue;
				case ("Short"):
					f.set(domain, Strings.isNullOrEmpty(value) ? 0 : Strings.valueOfShort(value));
					continue;
				case ("Double"):
					f.set(domain, Strings.isNullOrEmpty(value) ? 0D : Strings.valueOfDouble(value));
					continue;
				case ("Date"):
					f.set(domain,
							Strings.isNullOrEmpty(value) ? new Date() : Strings.valueOfDateTime(value).getLocalDate());
					continue;
				case ("Float"):
					f.set(domain, Strings.isNullOrEmpty(value) ? 0F : Strings.valueOfFloat(value));
					continue;
				default:
					log.warn("BaseDao.convertFrom(): Cannot convert field from request for "
							+ domainClass.getSimpleName() + "." + f.getType().getSimpleName());
					continue;
				}
			}
			return domain;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public T convertFrom(HttpServletRequest request) {
		return convertFrom(domainClass, request);
	}

	public T insertAndGet(HttpServletRequest request) {
		try {
			final StringBuilder sb = new StringBuilder("INSERT INTO ");
			sb.append(getTableName());
			sb.append(" (");
			boolean isAppend = false;
			for (Field f : domainClass.getFields()) {
				if (isIdGenerateIdentity() && f.getName().equals(getIdColumnName())) {
					continue;
				}
				String value = request.getParameter(f.getName());
				if (Strings.isNullOrEmpty(value)) {
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
				String value = request.getParameter(f.getName());
				if (Strings.isNullOrEmpty(value)) {
					continue;
				}
				if (isAppend) {
					sb.append(',');
					sb.append(' ');
				} else {
					isAppend = true;
				}
				sb.append('"');
				sb.append(value);
				sb.append('"');
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
				return findOne(keyHolder.getKey().toString());
			}
			int c = jdbcTemplate.update(sb.toString());
			if (c <= 0) {
				return null;
			}
			return findOne(request.getParameter(domainClass.getField(getIdColumnName()).getName()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int insertCheckNull(HttpServletRequest request) {
		return insert(convertFrom(request));
	}

	public int insert(HttpServletRequest request) {
		final StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(getTableName());
		sb.append(" (");
		boolean isAppend = false;
		for (Field f : domainClass.getFields()) {
			if (isIdGenerateIdentity() && f.getName().equals(getIdColumnName())) {
				continue;
			}
			String value = request.getParameter(f.getName());
			if (Strings.isNullOrEmpty(value)) {
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
			String value = request.getParameter(f.getName());
			if (Strings.isNullOrEmpty(value)) {
				continue;
			}
			if (isAppend) {
				sb.append(',');
				sb.append(' ');
			} else {
				isAppend = true;
			}
			sb.append('"');
			sb.append(value);
			sb.append('"');
		}
		sb.append(')');
		log.debug("INSERT: " + sb.toString());
		return jdbcTemplate.update(sb.toString());
	}

	//////////////////
	///
	//////////////////
	public T updateAndGet(HttpServletRequest request, String idValue) {
		final StringBuilder sb = new StringBuilder("UPDATE ");
		sb.append(getTableName());
		sb.append(" SET ");
		boolean isAppend = false;
		for (Field f : domainClass.getFields()) {
			if (isIdGenerateIdentity() && f.getName().equals(getIdColumnName())) {
				continue;
			}
			String value = request.getParameter(f.getName());
			if (Strings.isNullOrEmpty(value)) {
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
			sb.append('"');
			sb.append(value);
			sb.append('"');
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
		return findOne(idValue);
	}

	public int update(HttpServletRequest request, String idValue) {
		final StringBuilder sb = new StringBuilder("UPDATE ");
		sb.append(getTableName());
		sb.append(" SET ");
		boolean isAppend = false;
		for (Field f : domainClass.getFields()) {
			if (isIdGenerateIdentity() && f.getName().equals(getIdColumnName())) {
				continue;
			}
			String value = request.getParameter(f.getName());
			if (Strings.isNullOrEmpty(value)) {
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
			sb.append('"');
			sb.append(value);
			sb.append('"');
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
	}

	public int updateCheckNull(HttpServletRequest request, String idValue) {
		return update(convertFrom(request), idValue);
	}

}
