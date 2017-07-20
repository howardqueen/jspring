package com.jspring.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.persistence.JColumnValue;
import com.jspring.persistence.JTableValue;

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

	public static String serialized(List<Where> wheres) {
		return wheres.stream()//
				.map(a -> a.toString())//
				.reduce((a, b) -> a + ";" + b)//
				.orElseThrow(() -> Exceptions.newIllegalArgumentException("wheres"));
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

	public static List<Where> deserializeList(String serializedValue) {
		return Stream.of(serializedValue.split(";"))//
				.map(a -> new Where(a))//
				.collect(Collectors.toList());//
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

	private Where(String wheres) {
		int i = wheres.indexOf(',');
		if (i <= 0) {
			throw Exceptions.newIllegalArgumentException("Where", wheres);
		}
		this.fieldName = wheres.substring(0, i);
		int j = wheres.indexOf(',', i + 1);
		//
		this.func = j < 0 ? wheres.substring(i).toLowerCase() : wheres.substring(i, j).toLowerCase();
		switch (func) {
		case "gt":
			if (j < 0) {
				throw Exceptions.newNullArgumentException("Where.value", wheres);
			}
			this.value = wheres.substring(j + 1);
			this.operator = ">";
			break;
		case "ge":
			if (j < 0) {
				throw Exceptions.newNullArgumentException("Where.value", wheres);
			}
			this.value = wheres.substring(j + 1);
			this.operator = ">=";
			break;
		case "eq":
			if (j < 0) {
				throw Exceptions.newNullArgumentException("Where.value", wheres);
			}
			this.value = wheres.substring(j + 1);
			this.operator = "=";
			break;
		case "ne":
			if (j < 0) {
				throw Exceptions.newNullArgumentException("Where.value", wheres);
			}
			this.value = wheres.substring(j + 1);
			this.operator = "!=";
			break;
		case "se":
			if (j < 0) {
				throw Exceptions.newNullArgumentException("Where.value", wheres);
			}
			this.value = wheres.substring(j + 1);
			this.operator = "<=";
			break;
		case "sm":
			if (j < 0) {
				throw Exceptions.newNullArgumentException("Where.value", wheres);
			}
			this.value = wheres.substring(j + 1);
			this.operator = "<";
			break;
		case "nu":
			this.operator = "IS NULL";
			break;
		case "nn":
			this.operator = "NOT NULL";
			break;
		case "bt":
			if (j < 0) {
				throw Exceptions.newNullArgumentException("Where.values", wheres);
			}
			this.values = wheres.substring(j + 1).split(",");
			if (this.values.length != 2) {
				throw Exceptions.newIllegalArgumentException("Where.values", wheres, "length should be two");
			}
			this.operator = "BTWEEN";
			break;
		case "in":
			if (j < 0) {
				throw Exceptions.newNullArgumentException("Where.values", wheres);
			}
			this.values = wheres.substring(j + 1).split(",");
			this.operator = "IN";
			break;
		default:
			throw Exceptions.newIllegalArgumentException("Where.func", wheres);
		}
	}

	@Override
	public String toString() {
		if (null != value) {
			return fieldName + "," + func + "," + value;
		}
		if (null != values) {
			return fieldName + "," + func + "," + Strings.join(",", values);
		}
		return fieldName + "," + func;
	}

	public void append(JTableValue table, StringBuilder sql, List<Object> args) {
		JColumnValue jv = table.getColumnByFieldName(fieldName);
		sql.append(jv.getSQLColumnPre());
		sql.append('`');
		sql.append(jv.getColumnName());
		sql.append('`');
		sql.append(this.operator);
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
		this.func = "gt";
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
