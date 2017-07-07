package com.jspring.data;

import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.date.DateFormats;

public class JColumnValue {

	public static JColumnValue of(JTableValue jtableValue, Field field) {
		return new JColumnValue(jtableValue, field);
	}

	public final JColumnTypes type;
	public final JTableValue tableValue;
	public final Field field;
	// @Basic
	private final String name;
	public final String title;
	// @More
	public String indexName = "";
	public String defaultValue = "";
	private String _expression = "";
	public boolean findAllable = true;
	public boolean findOnable = true;
	// @Column
	public boolean unique = false;
	public boolean nullable = true;
	private boolean _insertable = true;
	private boolean _updatable = true;
	// @Join/Options
	public String table = "";
	public String options = "";
	public String joinOptions = "";
	public String joinColumn = "";
	// @Definition
	public int length = 255;
	public int precision = 0;
	public int scale = 0;

	private JColumnValue(JTableValue tableValue, Field field) {
		this.type = JColumnTypes.of(field);
		if (this.type == JColumnTypes.Unknown) {
			throw Exceptions.newIllegalArgumentException("Unsupport field type: " + tableValue.domain.getName() + "/["
					+ field.getType().getName() + "]" + field.getName());
		}
		this.tableValue = tableValue;
		this.field = field;
		//
		JColumn jc = field.getType().getAnnotation(JColumn.class);
		if (null == jc) {
			this.name = field.getName();
			this.title = this.name;
			return;
		}
		// @Basic
		this.name = Strings.isNullOrEmpty(jc.name()) ? field.getName() : jc.name();
		this.title = Strings.isNullOrEmpty(jc.title()) ? this.name : jc.title();
		//
		this.table = jc.table();
		this.options = jc.options();
		this.joinOptions = jc.joinOptions();
		this.joinColumn = jc.joinColumn();
		// @More
		this.indexName = jc.indexName();
		this.defaultValue = jc.defaultValue();
		this._expression = jc.expression();
		this.findAllable = jc.findAllable();
		this.findOnable = jc.findOnable();
		// @Column
		this.unique = jc.unique();
		this.nullable = jc.nullable();
		this._insertable = jc.insertable();
		this._updatable = jc.updatable();
		//
		this.length = jc.length();
		this.precision = jc.precision();
		this.scale = jc.scale();
	}

	public boolean isFilterable() {
		return Strings.isNullOrEmpty(_expression)//
				&& Strings.isNullOrEmpty(joinOptions);
	}

	public boolean isEditable() {// insert and update
		return Strings.isNullOrEmpty(_expression)//
				&& Strings.isNullOrEmpty(table)//
				&& Strings.isNullOrEmpty(joinOptions);
	}

	private String _sqlColumn;

	public String getColumnName() {
		return name;
	}

	public String getFieldName() {
		return field.getName();
	}

	public String getColumnSQL() {
		if (null != _sqlColumn) {
			return _sqlColumn;
		}
		if (!Strings.isNullOrEmpty(_expression)) {
			_sqlColumn = "(" + _expression + ") AS `" + name + "`";
			return _sqlColumn;

		}
		if (!Strings.isNullOrEmpty(table)) {
			_sqlColumn = "`" + table + "`.`" + name + "`";
			return _sqlColumn;
		}
		if (Strings.isNullOrEmpty(joinOptions)) {
			_sqlColumn = tableValue.getSQLColumnPre() + '`' + name + "`";
			return _sqlColumn;
		}
		if (Strings.isNullOrEmpty(joinColumn)) {
			throw Exceptions.newInstance(tableValue.domain.getName() + "/" + getFieldName()
					+ "/@JColumn: property \"joinColumn\" cannot be null when \"joinOptions\" contains value");
		}
		JoinOptions options = tableValue.getOptions(joinOptions);
		_sqlColumn = "(SELECT `" + options.textColumn() + "` FROM `" + options.name()//
				+ "` WHERE "
				+ (Strings.isNullOrEmpty(tableValue.getSQLColumnPre()) ? "`" + tableValue.getName() + "`."
						: tableValue.getSQLColumnPre())
				+ "`"//
				+ joinColumn + "`=`" + options.valueColumn() + "` LIMIT 1) AS `" + name + "`";
		return _sqlColumn;
	}

	private Boolean insertable;

	public boolean isInsertable() {
		if (null == insertable) {
			if (isEditable()) {
				insertable = Boolean.valueOf(_insertable);
			} else {
				insertable = Boolean.valueOf(false);
			}
		}
		return insertable.booleanValue();
	}

	private Boolean updatable;

	public boolean isUpdatable() {
		if (null == updatable) {
			if (isEditable()) {
				updatable = Boolean.valueOf(_updatable);
			} else {
				updatable = Boolean.valueOf(false);
			}
		}
		return updatable.booleanValue();
	}
	
	public String getColumnDefinition() {
		if (isEditable()) {
			switch (type) {
			case String:
				return '`' + name + "` varchar(50) "
						+ (nullable ? "DEFAULT NULL"
								: (Strings.isNullOrEmpty(defaultValue) ? "NOT NULL"
										: "NOT NULL DEFAULT '" + defaultValue + "'"));
			case Integer:
				return '`' + name + "` int(11) "
						+ (nullable ? "DEFAULT NULL"
								: (Strings.isNullOrEmpty(defaultValue) ? "NOT NULL"
										: "NOT NULL DEFAULT '" + defaultValue + "'"));
			case Long:
				return '`' + name + "` bigint(20) "
						+ (nullable ? "DEFAULT NULL"
								: (Strings.isNullOrEmpty(defaultValue) ? "NOT NULL"
										: "NOT NULL DEFAULT '" + defaultValue + "'"));
			case Short:
				return '`' + name + "` smallint(6) "
						+ (nullable ? "DEFAULT NULL"
								: (Strings.isNullOrEmpty(defaultValue) ? "NOT NULL"
										: "NOT NULL DEFAULT '" + defaultValue + "'"));
			case Double:
				return '`' + name + "` double(22) "
						+ (nullable ? "DEFAULT NULL"
								: (Strings.isNullOrEmpty(defaultValue) ? "NOT NULL"
										: "NOT NULL DEFAULT '" + defaultValue + "'"));
			case Float:
				return '`' + name + "` float(12) "
						+ (nullable ? "DEFAULT NULL"
								: (Strings.isNullOrEmpty(defaultValue) ? "NOT NULL"
										: "NOT NULL DEFAULT '" + defaultValue + "'"));
			case Boolean:
				return '`' + name + "` bit(1) "
						+ (nullable ? "DEFAULT NULL"
								: (Strings.isNullOrEmpty(defaultValue) ? "NOT NULL"
										: "NOT NULL DEFAULT b'" + defaultValue + "'"));
			default:// Date
				return '`' + name + "` timestamp "
						+ (nullable ? "NULL"
								: (Strings.isNullOrEmpty(defaultValue) ? "NOT NULL DEFAULT CURRENT_TIMESTAMP"
										: "NOT NULL DEFAULT '" + defaultValue + "'"));
			}
		}
		return null;
	}

	protected boolean isIdentity() {
		GeneratedValue v = field.getType().getAnnotation(GeneratedValue.class);
		return null != v && v.strategy() == GenerationType.IDENTITY;
	}

	public Object getValue(Object entity) {
		try {
			return field.get(entity);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public String getValueString(Object entity) {
		try {
			Object object = field.get(entity);
			if (null == object) {
				return "";
			}
			if (type == JColumnTypes.String) {
				return String.valueOf(object).replace('"', '\'').replaceAll("\r", "\\r").replaceAll("\n", "\\n");

			}
			if (type == JColumnTypes.Date) {
				return DateFormats.date.format((Date) object);
			}
			if (type == JColumnTypes.DateTime) {
				return DateFormats.dateTime.format((Date) object);
			}
			return String.valueOf(object);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public JColumnView createView() {
		return new JColumnView(this);
	}

}
