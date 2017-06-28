package com.jspring.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.UnaryOperator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.jdbc.core.RowMapper;

import com.jspring.Exceptions;
import com.jspring.Strings;

public class MetaField {

	public static <T> RowMapper<T> ROW_MAPPER(MetaEntity<T> metaEntity) {
		return (rs, i) -> {
			try {
				T domain = metaEntity.getEntityClass().newInstance();
				for (Field f : metaEntity.getEntityClass().getFields()) {
					if (Modifier.isStatic(f.getModifiers())) {
						continue;
					}
					switch (f.getType().getSimpleName()) {
					case ("String"):
						f.set(domain, rs.getString(metaEntity.getMetaField(f.getName()).getSqlColumnName()));
						continue;
					case ("Integer"):
						f.set(domain, rs.getInt(metaEntity.getMetaField(f.getName()).getSqlColumnName()));
						continue;
					case ("Long"):
						f.set(domain, rs.getLong(metaEntity.getMetaField(f.getName()).getSqlColumnName()));
						continue;
					case ("Date"):
						f.set(domain, rs.getTimestamp(metaEntity.getMetaField(f.getName()).getSqlColumnName()));
						continue;
					case ("Short"):
						f.set(domain, rs.getShort(metaEntity.getMetaField(f.getName()).getSqlColumnName()));
						continue;
					case ("Double"):
						f.set(domain, rs.getDouble(metaEntity.getMetaField(f.getName()).getSqlColumnName()));
						continue;
					case ("Float"):
						f.set(domain, rs.getFloat(metaEntity.getMetaField(f.getName()).getSqlColumnName()));
						continue;
					case ("Boolean"):
						f.set(domain, rs.getBoolean(metaEntity.getMetaField(f.getName()).getSqlColumnName()));
						continue;
					default:
						throw Exceptions.newInstance("Illegale FieldType " + metaEntity.getEntityClass().getSimpleName()
								+ "." + f.getType().getSimpleName());
					}
				}
				return domain;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	public static <T> RowMapper<T> ROW_MAPPER(Class<T> pojoClass) {
		return (rs, i) -> {
			try {
				T domain = pojoClass.newInstance();
				for (Field f : pojoClass.getFields()) {
					if (Modifier.isStatic(f.getModifiers())) {
						continue;
					}
					switch (f.getType().getSimpleName()) {
					case ("String"):
						f.set(domain, rs.getString(f.getName()));
						continue;
					case ("Integer"):
						f.set(domain, rs.getInt(f.getName()));
						continue;
					case ("Long"):
						f.set(domain, rs.getLong(f.getName()));
						continue;
					case ("Date"):
						f.set(domain, rs.getTimestamp(f.getName()));
						continue;
					case ("Short"):
						f.set(domain, rs.getShort(f.getName()));
						continue;
					case ("Double"):
						f.set(domain, rs.getDouble(f.getName()));
						continue;
					case ("Float"):
						f.set(domain, rs.getFloat(f.getName()));
						continue;
					case ("Boolean"):
						f.set(domain, rs.getBoolean(f.getName()));
						continue;
					default:
						throw Exceptions.newInstance("executePojos():Illegale FieldType " + pojoClass.getSimpleName()
								+ "." + f.getType().getSimpleName());
					}
				}
				return domain;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	public static <T> T MAPPER(MetaEntity<T> metaEntity, UnaryOperator<String> map) {
		try {
			T domain = metaEntity.getEntityClass().newInstance();
			for (Field f : metaEntity.getEntityClass().getFields()) {
				if (Modifier.isStatic(f.getModifiers())) {
					continue;
				}
				String value = map.apply(metaEntity.getMetaField(f.getName()).getSqlColumnName());
				if (Strings.isNullOrEmpty(value)) {
					continue;
				}
				switch (f.getType().getSimpleName()) {
				case ("String"):
					f.set(domain, value);
					continue;
				case ("Integer"):
					f.set(domain, Strings.valueOfInt(value));
					continue;
				case ("Long"):
					f.set(domain, Strings.valueOfLong(value));
					continue;
				case ("Short"):
					f.set(domain, Strings.valueOfShort(value));
					continue;
				case ("Double"):
					f.set(domain, Strings.valueOfDouble(value));
					continue;
				case ("Date"):
					f.set(domain, Strings.valueOfDateTime(value).getLocalDate());
					continue;
				case ("Float"):
					f.set(domain, Strings.valueOfFloat(value));
					continue;
				case ("Boolean"):
					f.set(domain, Strings.valueOfBool(value));
					continue;
				default:
					throw Exceptions.newInstance("Cannot convert field from map for "
							+ metaEntity.getEntityClass().getSimpleName() + "." + f.getType().getSimpleName());
				}
			}
			return domain;
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	private final Field field;

	public String getFieldName() {
		return field.getName();
	}

	public MetaField(Field field) {
		this.field = field;
	}

	private String _expression;

	public String getSqlColumnExpression() {
		if (null == _expression) {
			Column column = field.getAnnotation(Column.class);
			if (null == column || Strings.isNullOrEmpty(column.name())) {
				_expression = '`' + field.getName() + '`';
			} else {
				_expression = column.name();
			}
		}
		return _expression;
	}

	private String _name;

	public String getSqlColumnName() {
		if (null == _name) {
			Column column = field.getAnnotation(Column.class);
			if (null == column //
					|| Strings.isNullOrEmpty(column.name())//
					|| column.name().indexOf(' ') > 0) {// 含别名
				_name = field.getName();
			} else {
				_name = column.name();
			}
		}
		return _name;
	}

	/**
	 * 是否只读（不可创建和更新）。 含有别名的列将视作只读。
	 * 
	 * @return
	 */
	public boolean isReadonly() {
		Column column = field.getAnnotation(Column.class);
		if (null != column //
				&& !Strings.isNullOrEmpty(column.name())//
				&& column.name().indexOf(' ') > 0) {// 含别名
			return true;
		}
		return false;
	}

	private Boolean _isIdColumn = null;

	public boolean isIdColumn() {
		if (null == _isIdColumn) {
			Id v = field.getAnnotation(Id.class);
			if (null != v) {
				_isIdColumn = true;
			} else {
				_isIdColumn = false;
			}
		}
		return _isIdColumn.booleanValue();
	}

	private Boolean _isIdGenerateIdentity = null;

	protected boolean isIdGenerateIdentity() {
		if (null == _isIdGenerateIdentity) {
			GeneratedValue v = field.getAnnotation(GeneratedValue.class);
			if (null != v && v.strategy() == GenerationType.IDENTITY) {
				_isIdGenerateIdentity = true;
			} else {
				_isIdGenerateIdentity = false;
			}
		}
		return _isIdGenerateIdentity.booleanValue();
	}

	public String getSchema() {
		// TODO
		return null;
	}

	public Object getSqlColumnValue(Object entity) {
		try {
			Object obj = field.get(entity);
			return obj;
			// if (null == obj) {
			// Column cl = field.getAnnotation(Column.class);
			// if (null == cl || cl.nullable()) {// Nullable
			// return "NULL";
			// }
			// switch (field.getType().getSimpleName()) {
			// case ("String"):
			// return "''";
			// case ("Integer"):
			// return "'0'";
			// case ("Long"):
			// return "'0'";
			// case ("Short"):
			// return "'0'";
			// case ("Double"):
			// return "'0'";
			// case ("Date"):
			// return '"' + DateTime.getNow().toString() + '"';
			// case ("Float"):
			// return "'0'";
			// case ("Boolean"):
			// return new Byte("0");
			// default:
			// throw Exceptions.newInstance("Unsupport fieldType " +
			// entity.getClass().getSimpleName() + "."
			// + field.getType().getSimpleName());
			// }
			// }
			// switch (field.getType().getSimpleName()) {
			// case ("Date"):
			// return '"' + DateFormats.dateTime.format((Date) obj) + '"';
			// case ("Boolean"):
			// return ((Boolean) obj).booleanValue() ? new Byte("1") : new
			// Byte("0");
			// default:
			// return obj;
			// }
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

}
