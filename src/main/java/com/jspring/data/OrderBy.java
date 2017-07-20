package com.jspring.data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.persistence.JColumnValue;
import com.jspring.persistence.JTableValue;

public class OrderBy {

	//////////////////
	/// ORDER BY
	//////////////////
	public static OrderBy of(Enum<?> column) {
		return new OrderBy(column);
	}

	public static OrderBy of(JColumnValue column) {
		return new OrderBy(column);
	}

	public static String serialize(OrderBy[] orders) {
		return Stream.of(orders)//
				.map(a -> a.toString())//
				.reduce((a, b) -> a + ";" + b)//
				.orElseThrow(() -> Exceptions.newIllegalArgumentException("orders"));
	}

	public static OrderBy[] deserialize(String serializedValue) {
		return Stream.of(serializedValue.split(";"))//
				.map(a -> new OrderBy(a))//
				.collect(Collectors.toList())//
				.toArray(new OrderBy[0]);
	}

	public static void appendTo(StringBuilder sql, JTableValue table, OrderBy... orders) {
		if (null == orders || orders.length == 0) {
			return;
		}
		orders[0].append(sql, table);
		for (int i = 1; i < orders.length; i++) {
			sql.append(',');
			orders[i].append(sql, table);
		}
	}

	public static void appendTo(StringBuilder sql, JTableValue table, String serializedValue) {
		appendTo(sql, table, deserialize(serializedValue));
	}

	//////////////////
	/// ORDER BY
	//////////////////
	private final String fieldName;

	private String operator;

	private OrderBy(Enum<?> column) {
		this.fieldName = column.toString();
		this.asc();
	}

	private OrderBy(JColumnValue column) {
		this.fieldName = column.getFieldName();
	}

	private OrderBy(String sorts) {
		int i = sorts.indexOf(',');
		if (i <= 0) {
			throw Exceptions.newIllegalArgumentException("OrderBy", sorts);
		}
		this.fieldName = sorts.substring(0, i);
		this.operator = sorts.substring(i + 1).toUpperCase();
		if (Strings.isNullOrEmpty(operator)) {
			operator = "ASC";
			return;
		}
		if (!operator.equals("ASC") || !operator.equals("DESC")) {
			throw Exceptions.newIllegalArgumentException("OrderBy.operator", sorts, "should be \"ASC\" or \"DESC\"");
		}
	}

	@Override
	public String toString() {
		return this.fieldName + "," + this.operator;
	}

	public void append(StringBuilder sql, JTableValue table) {
		JColumnValue jv = table.getColumnByFieldName(fieldName);
		sql.append(jv.getSQLColumnPre());
		sql.append('`');
		sql.append(jv.getColumnName());
		sql.append('`');
		sql.append(" ");
		sql.append(this.operator);
	}

	public OrderBy asc() {
		this.operator = "ASC";
		return this;
	}

	public OrderBy desc() {
		this.operator = "DESC";
		return this;
	}

}
