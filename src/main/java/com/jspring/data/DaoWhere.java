package com.jspring.data;

import java.util.ArrayList;

import com.jspring.Exceptions;
import com.jspring.Strings;

/**
 * @author hqian
 * 
 */
public class DaoWhere {

	public static enum Operators {

		Equal("eq", "="), NotEqual("ne", "!="), Smaller("sm", "<"), SmallerOrEqual("se", "<="), Greater("gr",
				">"), GreaterOrEqual("ge", ">="), Like("lk", "LIKE");

		public final String shortName;
		public final String operator;

		private Operators(String shortName, String operator) {
			this.shortName = shortName;
			this.operator = operator;
		}

		@Override
		public String toString() {
			return this.operator;
		}

		public static Operators parse(String shortName) {
			for (Operators o : Operators.values()) {
				if (o.shortName.equalsIgnoreCase(shortName)) {
					return o;
				}
			}
			throw Exceptions.newInstance("Illegal short name for QueryFilter.Operators: " + shortName);
		}

	}

	public final String column;
	public final Operators operator;
	public final String value;

	public DaoWhere(String column, Operators operator, String value) {
		this.column = column;
		this.operator = operator;
		this.value = value;
	}

	@Override
	public String toString() {
		return toJoinString(this);
	}

	private static String toJoinString(DaoWhere q) {
		return String.format("%s,%s,%s", q.column, q.operator, q.value);
	}

	private static DaoWhere fromJoinString(String joinString) {
		String[] t = joinString.split(",");
		if (t.length < 3) {
			return null;
		}
		return new DaoWhere(t[0], Operators.parse(t[1]), t[2]);
	}

	public static String toJoinStrings(DaoWhere... qs) {
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

	public static DaoWhere[] fromJoinStrings(String joinStrings) {
		if (Strings.isNullOrEmpty(joinStrings)) {
			return new DaoWhere[0];
		}
		String[] t = joinStrings.split(";");
		ArrayList<DaoWhere> ls = new ArrayList<DaoWhere>();
		for (int i = 0; i < t.length; i++) {
			DaoWhere tt = fromJoinString(t[i]);
			if (null != tt) {
				ls.add(tt);
			}
		}
		return ls.toArray(new DaoWhere[0]);
	}

}
