package com.jspring.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jspring.Exceptions;
import com.jspring.Strings;

public class Where {

	//////////////////
	/// WHERE
	//////////////////
	public static Where of(Enum<?> column) {
		return new Where(column);
	}

	public static Where of(JColumnValue column) {
		return new Where(column);
	}

	public static String serialized(Where[] wheres) {
		return Stream.of(wheres)//
				.map(a -> a.toString())//
				.reduce((a, b) -> a + ";" + b)//
				.orElseThrow(() -> Exceptions.newIllegalArgumentException("wheres"));
	}

	public static Where[] deserialize(String serializedValue) {
		return Stream.of(serializedValue.split(";"))//
				.map(a -> new Where(a))//
				.collect(Collectors.toList())//
				.toArray(new Where[0]);
	}

	public static void appendTo(StringBuilder sql, List<Object> args, JTableValue table, Where... wheres) {
		if (null == wheres || wheres.length == 0) {
			return;
		}
		wheres[0].append(table, sql, args);
		for (int i = 1; i < wheres.length; i++) {
			sql.append(" AND ");
			wheres[i].append(table, sql, args);
		}
	}

	public static void appendTo(StringBuilder sql, List<Object> args, JTableValue table, String serializedValue) {
		appendTo(sql, args, table, deserialize(serializedValue));
	}

	//////////////////
	/// WHERE
	//////////////////
	private final String fieldName;
	private String operator;
	private String func;
	private Object value;
	private Object[] values;

	private Where(Enum<?> column) {
		this.fieldName = column.toString();
	}

	private Where(JColumnValue column) {
		this.fieldName = column.getFieldName();
	}

	private Where(String serializedValue) {
		int i = serializedValue.indexOf('(');
		int j = serializedValue.lastIndexOf(')');
		//
		this.func = serializedValue.substring(0, i);
		switch (func.toLowerCase()) {
		case "gr":
			this.operator = ">";
			break;
		case "ge":
			this.operator = ">=";
			break;
		case "eq":
			this.operator = "=";
			break;
		case "ne":
			this.operator = "!=";
			break;
		case "se":
			this.operator = "<=";
			break;
		case "sm":
			this.operator = "<";
			break;
		case "nu":
			this.operator = "IS NULL";
			break;
		case "nn":
			this.operator = "NOT NULL";
			break;
		case "bt":
			this.operator = "BTWEEN";
			break;
		case "in":
			this.operator = "IN";
			break;
		default:
			throw Exceptions.newInstance("Unknown where func: " + func);
		}
		String[] t = serializedValue.substring(i + 1, j).split(",");
		if (null == t || t.length == 0) {
			throw Exceptions.newInstance("Unknown where column: " + serializedValue);
		}
		this.fieldName = t[0];
		if (t.length == 1) {
			return;
		}
		if (t.length == 2) {
			this.value = t[1];
			return;
		}
		this.values = new Object[t.length - 1];
		for (i = 1; i < t.length; i++) {
			this.values[i - 1] = t[i];
		}
	}

	@Override
	public String toString() {
		if (null != value) {
			return func + "(" + this.fieldName + "," + value + ")";
		}
		if (null != values) {
			return func + "(" + this.fieldName + "," + Strings.join(",", values) + ")";
		}
		return func + "(" + this.fieldName + ")";
	}

	public void append(JTableValue table, StringBuilder sql, List<Object> args) {
		sql.append('`');
		sql.append(table.getColumnByFieldName(fieldName).getColumnName());
		sql.append('`');
		sql.append(' ');
		sql.append(this.operator);
		sql.append(' ');
		if (null != value) {
			sql.append('?');
			args.add(value);
		} else if (null != values) {
			if (this.operator.equals("BETWEEN")) {
				sql.append('?');
				sql.append(" AND ");
				sql.append('?');
				args.add(values[0]);
				args.add(values[1]);
			} else {
				sql.append('(');
				sql.append('?');
				for (int i = 1; i < args.size(); i++) {
					sql.append(',');
					sql.append('?');
				}
				sql.append(')');
				args.addAll(Arrays.asList(values));
			}
		}
	}

	public Where greaterThan(Object value) {
		this.operator = ">";
		this.func = "gr";
		this.value = value;
		this.values = null;
		return this;
	}

	public Where greaterEqual(Object value) {
		this.operator = ">=";
		this.func = "ge";
		this.value = value;
		this.values = null;
		return this;
	}

	public Where equalWith(Object value) {
		this.operator = "=";
		this.func = "eq";
		this.value = value;
		this.values = null;
		return this;
	}

	public Where notEqualWith(Object value) {
		this.operator = "!=";
		this.func = "ne";
		this.value = value;
		this.values = null;
		return this;
	}

	public Where smallerEqual(Object value) {
		this.operator = "<=";
		this.func = "se";
		this.value = value;
		this.values = null;
		return this;
	}

	public Where smallerThan(Object value) {
		this.operator = "<";
		this.func = "sm";
		this.value = value;
		this.values = null;
		return this;
	}

	public Where isNull() {
		this.operator = "IS NULL";
		this.func = "nu";
		this.value = null;
		this.values = null;
		return this;
	}

	public Where notNull() {
		this.operator = "NOT NULL";
		this.func = "nn";
		this.value = null;
		this.values = null;
		return this;
	}

	public Where between(Object min, Object max) {
		this.operator = "BETWEEN";
		this.func = "bt";
		this.value = null;
		this.values = new Object[] { min, max };
		return this;
	}

	public Where in(Object[] values) {
		if (null == values || values.length == 0) {
			throw Exceptions.newInstance("[SQL]where in (args): args can't be null");
		}
		this.operator = "IN";
		this.func = "in";
		this.value = null;
		this.values = values;
		return this;
	}

}
