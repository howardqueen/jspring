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
	public final JTableValue tableData;
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
	// (select name from referencedTable where xxx limit 1)
	public String referencedTable = "";
	// (select name from referencedTable where rt.referencedName =
	// joinColumn limit 1)
	public String referencedName = "";
	public String joinColumn = "";
	//
	public int length = 255;
	public int precision = 0;
	public int scale = 0;

	private JColumnValue(JTableValue table, Field field) {
		this.type = JColumnTypes.of(field);
		if (this.type == JColumnTypes.Unknown) {
			throw Exceptions.newIllegalArgumentException("Unsupport field type: " + table.domain.getName() + "/["
					+ field.getType().getName() + "]" + field.getName());
		}
		this.tableData = table;
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
		this.referencedTable = jc.referencedTable();
		this.referencedName = jc.referencedColumn();
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
				&& (Strings.isNullOrEmpty(referencedTable) //
						|| Strings.isNullOrEmpty(referencedName));
	}

	public boolean isEditable() {// insert and update
		return Strings.isNullOrEmpty(_expression)//
				&& Strings.isNullOrEmpty(referencedTable);
	}

	private String _sqlColumn;

	public String getColumnName() {
		return name;
	}

	public String getFieldName() {
		return field.getName();
	}

	public String getSQLColumn() {
		if (null != _sqlColumn) {
			return _sqlColumn;
		}
		if (!Strings.isNullOrEmpty(_expression)) {
			_sqlColumn = "(" + _expression + ") AS `" + name + "`";
			return _sqlColumn;

		}
		if (Strings.isNullOrEmpty(referencedTable)) {
			_sqlColumn = tableData.getSQLColumnPre() + '`' + name + "`";
			return _sqlColumn;
		}
		if (Strings.isNullOrEmpty(referencedName)) {
			_sqlColumn = "`" + referencedTable + "`.`" + name + "`";
			return _sqlColumn;
		}
		if (Strings.isNullOrEmpty(joinColumn)) {
			throw Exceptions.newInstance("[" + tableData.domain.getName() + "]." + field.getName()
					+ ": @JColumn field joinColumn cannot be null when referencedName has value.");
		}
		//
		_sqlColumn = "(SELECT `" + name + "` FROM `" + referencedTable//
				+ "` WHERE "
				+ (Strings.isNullOrEmpty(tableData.getSQLColumnPre()) ? "`" + tableData.getName() + "`."
						: tableData.getSQLColumnPre())
				+ "`"//
				+ joinColumn + "`=`" + referencedName + "` LIMIT 1) AS `" + name + "`";
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
