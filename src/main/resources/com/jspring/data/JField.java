//package com.jspring.data;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.util.function.UnaryOperator;
//
//import javax.persistence.Column;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//
//import org.springframework.jdbc.core.RowMapper;
//
//import com.jspring.Exceptions;
//import com.jspring.Strings;
//
//public class JField {
//
//	public static final String TYPE_STRING = "String", //
//			TYPE_INTEGER = "Integer", //
//			TYPE_LONG = "Long", //
//			TYPE_SHORT = "Short", //
//			TYPE_DOUBLE = "Double", //
//			TYPE_FLOAT = "Float", //
//			//
//			TYPE_BOOLEAN = "Boolean", //
//			//
//			TYPE_DATE = "Date"; //
//
//	
//
//	// public static Object getValue4SQL(JEntity<?> jentity, JField jfield) {
//	// for (Field f : jentity.getClass().getFields()) {
//	// if (Modifier.isStatic(f.getModifiers())) {
//	// continue;
//	// }
//	// switch (f.getType().getSimpleName()) {
//	// case (TYPE_STRING):
//	// f.set(domain, rs.getString(f.getName()));
//	// continue;
//	// case (TYPE_INTEGER):
//	// f.set(domain, rs.getInt(f.getName()));
//	// continue;
//	// case (TYPE_LONG):
//	// f.set(domain, rs.getLong(f.getName()));
//	// continue;
//	// case (TYPE_DATE):
//	// f.set(domain, rs.getTimestamp(f.getName()));
//	// continue;
//	// case (TYPE_SHORT):
//	// f.set(domain, rs.getShort(f.getName()));
//	// continue;
//	// case (TYPE_DOUBLE):
//	// f.set(domain, rs.getDouble(f.getName()));
//	// continue;
//	// case (TYPE_FLOAT):
//	// f.set(domain, rs.getFloat(f.getName()));
//	// continue;
//	// case (TYPE_BOOLEAN):
//	// f.set(domain, rs.getBoolean(f.getName()));
//	// continue;
//	// default:
//	// throw Exceptions.newInstance("executePojos():Illegale FieldType " +
//	// pojoClass.getSimpleName() + "."
//	// + f.getType().getSimpleName());
//	// }
//	// }
//	// }
//
//	private final Field localField;
//	public final JColumns column;
//
//	public JField(Field field) {
//		this.localField = field;
//		this.column = JColumns.of(field);
//	}
//
////	public String getFieldName() {
////		return localField.getName();
////	}
////
////	public String getFieldType() {
////		return localField.getType().getSimpleName();
////	}
//
//	private String _expression;
//
//	public String getExpression4SQL() {
//		if (null == _expression) {
//			Column column = localField.getAnnotation(Column.class);
//			if (null == column || Strings.isNullOrEmpty(column.name())) {
//				_expression = '`' + localField.getName() + '`';
//			} else {
//				_expression = column.name();
//			}
//		}
//		return _expression;
//	}
//
//	private String _name;
//
//	public String getName4SQL() {
//		if (null == _name) {
//			Column column = localField.getAnnotation(Column.class);
//			if (null == column //
//					|| Strings.isNullOrEmpty(column.name())//
//					|| column.name().indexOf(' ') > 0) {// 含别名
//				_name = localField.getName();
//			} else {
//				_name = column.name();
//			}
//		}
//		return _name;
//	}
//
//	public boolean findAllable() {
//		Column column = localField.getAnnotation(Column.class);
//		if (null != column //
//				&& !Strings.isNullOrEmpty(column.name())//
//				&& column.name().indexOf(' ') > 0) {// 含别名
//			return true;
//		}
//		return false;
//	}
//
//	public boolean findOneable() {
//		Column column = localField.getAnnotation(Column.class);
//		if (null != column //
//				&& !Strings.isNullOrEmpty(column.name())//
//				&& column.name().indexOf(' ') > 0) {// 含别名
//			return true;
//		}
//		return false;
//	}
//
//	public boolean insertable() {
//		Column column = localField.getAnnotation(Column.class);
//		if (null != column //
//				&& !Strings.isNullOrEmpty(column.name())//
//				&& column.name().indexOf(' ') > 0) {// 含别名
//			return true;
//		}
//		return false;
//	}
//
//	public boolean updateable() {
//		Column column = localField.getAnnotation(Column.class);
//		if (null != column //
//				&& !Strings.isNullOrEmpty(column.name())//
//				&& column.name().indexOf(' ') > 0) {// 含别名
//			return true;
//		}
//		return false;
//	}
//
//	private Boolean _isIdColumn = null;
//
//	public boolean isIdColumn() {
//		if (null == _isIdColumn) {
//			Id v = localField.getAnnotation(Id.class);
//			if (null != v) {
//				_isIdColumn = true;
//			} else {
//				_isIdColumn = false;
//			}
//		}
//		return _isIdColumn.booleanValue();
//	}
//
//	private Boolean _isIdGenerateIdentity = null;
//
//	protected boolean isIdGenerateIdentity() {
//		if (null == _isIdGenerateIdentity) {
//			GeneratedValue v = localField.getAnnotation(GeneratedValue.class);
//			if (null != v && v.strategy() == GenerationType.IDENTITY) {
//				_isIdGenerateIdentity = true;
//			} else {
//				_isIdGenerateIdentity = false;
//			}
//		}
//		return _isIdGenerateIdentity.booleanValue();
//	}
//
//	public String getSchema() {
//		// TODO
//		return null;
//	}
//
//	public Object getSqlColumnValue(Object entity) {
//		try {
//			Object obj = localField.get(entity);
//			return obj;
//		} catch (Exception e) {
//			throw Exceptions.newInstance(e);
//		}
//	}
//
//}
