package com.jspring.data;

import com.jspring.Strings;

/**
 * @author hqian
 * 
 */
public class DaoOrder {

	public static enum OrderTypes {
		Asc("ASC"), Desc("DESC");
		private final String value;

		private OrderTypes(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static OrderTypes parse(String value) {
			if (Desc.value.equalsIgnoreCase(value)) {
				return Desc;
			}
			return Asc;
		}

	}

	public final String column;
	public final OrderTypes type;

	public DaoOrder(String column) {
		this.column = column;
		this.type = OrderTypes.Asc;
	}

	private DaoOrder(String column, OrderTypes type) {
		this.column = column;
		this.type = type;
	}

	public DaoOrder(Enum<?> column, OrderTypes type) {
		this.column = column.toString();
		this.type = type;
	}

	@Override
	public String toString() {
		return toJoinString(this);
	}

	private static String toJoinString(DaoOrder q) {
		return q.column + "," + q.type;
	}

	private static DaoOrder fromJoinString(String joinString) {
		String[] t = joinString.split(",");
		return new DaoOrder(t[0], OrderTypes.parse(t[1]));
	}

	public static String toJoinStrings(DaoOrder... qs) {
		if (null == qs || qs.length == 0) {
			return "";
		}
		if (qs.length == 1) {
			return toJoinString(qs[0]);
		}
		StringBuilder sb = new StringBuilder(toJoinString(qs[0]));
		for (int i = 1; i < qs.length; i++) {
			sb.append(";");
			sb.append(toJoinString(qs[i]));
		}
		return sb.toString();
	}

	public static DaoOrder[] fromJoinStrings(String joinStrings) {
		if (Strings.isNullOrEmpty(joinStrings)) {
			return new DaoOrder[0];
		}
		String[] t = joinStrings.split(";");
		DaoOrder[] qs = new DaoOrder[t.length];
		for (int i = 0; i < qs.length; i++) {
			qs[i] = fromJoinString(t[i]);
		}
		return qs;
	}

}
