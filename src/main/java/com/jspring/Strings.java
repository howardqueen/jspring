package com.jspring;

import java.util.ArrayList;

import com.jspring.date.DateTime;

public abstract class Strings {

	private Strings() {
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static boolean isNullOrEmpty(String value) {
		return null == value || value.length() == 0;
	}

	public static String getThumbnail(String value, String nullValue, int maxLength) {
		if (null == value || value.length() == 0) {
			return nullValue;
		}
		return value.length() > maxLength ? value.substring(0, maxLength - 3) + "..." : value;
	}

	public static boolean isNumeric(String value) {
		if (null == value || value.length() == 0) {
			return false;
		}
		for (int i = 0; i < value.length(); i++) {
			if (!Character.isDigit(value.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean containUpperCase(String value) {
		if (null == value || value.length() == 0) {
			return false;
		}
		for (int i = 0; i < value.length(); i++) {
			if ('A' <= value.charAt(i) && value.charAt(i) <= 'Z') {
				return true;
			}
		}
		return false;
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static String changeEncoding(String value, Encodings oriEncoding, Encodings newEncoding) {
		if (isNullOrEmpty(value)) {
			return value;
		}
		try {
			return new String(value.getBytes(oriEncoding.value), newEncoding.value);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static String changeISO_8859_1ToUTF8(String value) {
		return changeEncoding(value, Encodings.ISO_8859_1, Encodings.UTF8);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static String join(String split, int... values) {
		if (null == values || values.length == 0) {
			return "";
		}
		if (null == split) {
			split = "";
		}
		StringBuilder sb = new StringBuilder();
		for (Object v : values) {
			sb.append(String.valueOf(v));
			sb.append(split);
		}
		return sb.substring(0, sb.length() - split.length());
	}

	public static String join(String split, String... values) {
		if (null == values || values.length == 0) {
			return "";
		}
		if (null == split) {
			split = "";
		}
		StringBuilder sb = new StringBuilder();
		for (String v : values) {
			if (null != v) {
				sb.append(v);
			}
			sb.append(split);
		}
		return sb.substring(0, sb.length() - split.length());
	}

	public static String join(String split, Object... values) {
		if (null == values || values.length == 0) {
			return "";
		}
		if (null == split) {
			split = "";
		}
		StringBuilder sb = new StringBuilder();
		for (Object v : values) {
			if (null != v) {
				sb.append(v.toString());
			} else {
				sb.append("");
			}
			sb.append(split);
		}
		return sb.substring(0, sb.length() - split.length());
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static String[] split(String source, String split, boolean skipEmpty) {
		if (Strings.isNullOrEmpty(source)) {
			return new String[0];
		}
		ArrayList<String> ls = new ArrayList<String>();
		int i = 0, j = source.indexOf(split);
		while (j > 0) {
			if (!skipEmpty || j - i > 1) {
				ls.add(source.substring(i, j));
			}
			i = j + split.length();
			j = source.indexOf(split, i);
		}
		return ls.toArray(new String[ls.size()]);
	}

	public static String[] split(String source, String split) {
		return split(source, split, false);
	}

	public static String[] split(String source, char split, boolean skipEmpty) {
		if (Strings.isNullOrEmpty(source)) {
			return new String[0];
		}
		ArrayList<String> ls = new ArrayList<String>();
		int i = 0, j = source.indexOf(split);
		while (j > 0) {
			if (!skipEmpty || j - i > 1) {
				ls.add(source.substring(i, j));
			}
			ls.add(source.substring(i, j));
			i = j + 1;
			j = source.indexOf(split, i);
		}
		return ls.toArray(new String[ls.size()]);
	}

	public static String[] split(String source, char split) {
		return split(source, split, false);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static String[] getLines(String source) {
		String[] t = source.split("\n");
		if (t.length <= 1) {
			return source.split("\r\n");
		}
		return t;
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	/* about substring */
	public static class SubstringItem {
		public final int nextFromIndex;
		public final String value;

		public SubstringItem(String value, int nextFromIndex) {
			this.nextFromIndex = nextFromIndex;
			this.value = value;
		}

		public SubstringItem(String value) {
			this(value, -1);
		}
	}

	public static class SubstringItems {
		public final int nextFromIndex;
		public final String[] values;

		public SubstringItems(String[] values, int nextFromIndex) {
			this.nextFromIndex = nextFromIndex;
			this.values = values;
		}

		public SubstringItems(String[] values) {
			this(values, -1);
		}
	}

	public abstract SubstringItem getSubstring(String source, int fromIndex);

	public SubstringItem getSubstring(String source) {
		return getSubstring(source, 0);
	}

	public SubstringItems getSubstrings(String source, int fromIndex, int count) {
		if (count <= 0) {
			count = Integer.MAX_VALUE;
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		ArrayList<String> rs = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			if (fromIndex < 0) {
				return new SubstringItems(rs.toArray(new String[rs.size()]));
			}
			SubstringItem holder = getSubstring(source, fromIndex);
			if (null == holder) {
				return new SubstringItems(rs.toArray(new String[rs.size()]));
			}
			rs.add(holder.value);
			fromIndex = holder.nextFromIndex;
		}
		return new SubstringItems(rs.toArray(new String[rs.size()]));
	}

	public SubstringItems getSubstringsByFromIndex(String source, int fromIndex) {
		return getSubstrings(source, fromIndex, 0);
	}

	public SubstringItems getSubstringsByCount(String source, int count) {
		return getSubstrings(source, 0, count);
	}

	public SubstringItems getSubstrings(String source) {
		return getSubstrings(source, 0, 0);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	private static class SubstringAnalyzerStr extends Strings {

		public final String head;
		public final String tail;

		public SubstringAnalyzerStr(String head, String tail) {
			this.head = head;
			this.tail = tail;
		}

		@Override
		public String toString() {
			return String.format("<%s,%s>", this.head, this.tail);
		}

		@Override
		public SubstringItem getSubstring(String source, int fromIndex) {
			int i = source.indexOf(this.head, fromIndex);
			if (i < 0) {
				return null;
			}
			i += this.head.length();
			int j = source.indexOf(this.tail, i);
			if (j < 0) {
				return new SubstringItem(source.substring(i));
			}
			return new SubstringItem(source.substring(i, j), j + this.tail.length());
		}
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	private static class SubstringAnalyzerStrChar extends Strings {
		public final String head;
		public final char tail;

		public SubstringAnalyzerStrChar(String head, char tail) {
			this.head = head;
			this.tail = tail;
		}

		@Override
		public String toString() {
			return String.format("<%s,%s>", this.head, String.valueOf(this.tail));
		}

		@Override
		public SubstringItem getSubstring(String source, int fromIndex) {
			int i = source.indexOf(this.head, fromIndex);
			if (i < 0) {
				return null;
			}
			i += this.head.length();
			int j = source.indexOf(this.tail, i);
			if (j < 0) {
				return new SubstringItem(source.substring(i));
			}
			return new SubstringItem(source.substring(i, j), j + 1);
		}

	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	private static class SubstringAnalyzerCharStr extends Strings {
		public final char head;
		public final String tail;

		public SubstringAnalyzerCharStr(char head, String tail) {
			this.head = head;
			this.tail = tail;
		}

		@Override
		public String toString() {
			return String.format("<%s,%s>", String.valueOf(this.head), this.tail);
		}

		@Override
		public SubstringItem getSubstring(String source, int fromIndex) {
			int i = source.indexOf(this.head, fromIndex);
			if (i < 0) {
				return null;
			}
			i += 1;
			int j = source.indexOf(this.tail, i);
			if (j < 0) {
				return new SubstringItem(source.substring(i));
			}
			return new SubstringItem(source.substring(i, j), this.tail.length());
		}

	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	private static class SubstringAnalyzerChar extends Strings {
		public final char head;
		public final char tail;

		public SubstringAnalyzerChar(char head, char tail) {
			this.head = head;
			this.tail = tail;
		}

		@Override
		public String toString() {
			return String.format("<%s,%s>", String.valueOf(this.head), String.valueOf(this.tail));
		}

		@Override
		public SubstringItem getSubstring(String source, int fromIndex) {
			int i = source.indexOf(this.head, fromIndex);
			if (i < 0) {
				return null;
			}
			i += 1;
			int j = source.indexOf(this.tail, i);
			if (j < 0) {
				return new SubstringItem(source.substring(i));
			}
			return new SubstringItem(source.substring(i, j), j + 1);
		}

	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static Strings newSubstringAnalyzer(String head, String tail) {
		return new SubstringAnalyzerStr(head, tail);
	}

	public static Strings newSubstringAnalyzer(String head, char tail) {
		return new SubstringAnalyzerStrChar(head, tail);
	}

	public static Strings newSubstringAnalyzer(char head, String tail) {
		return new SubstringAnalyzerCharStr(head, tail);
	}

	public static Strings newSubstringAnalyzer(char head, char tail) {
		return new SubstringAnalyzerChar(head, tail);
	}

	private static Exceptions newSubstringException(Strings substrings, String source) {
		return Exceptions.newInstance("%s not found from: %s", substrings.toString(),
				source.length() > 30 ? source.substring(0, 30) + "..." : source);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	/* try get substring */
	public static String[] tryGetSubstrings(String source, int fromIndex, Strings... analyzers) {
		String[] rs = new String[analyzers.length];
		int fi = fromIndex < 0 ? 0 : fromIndex;
		for (int i = 0; i < analyzers.length; i++) {
			if (fi < 0) {
				rs[i] = null;
				continue;
			}
			SubstringItem holder = analyzers[i].getSubstring(source, fi);
			if (null == holder) {
				rs[i] = null;
				continue;
			}
			rs[i] = holder.value;
			fi = holder.nextFromIndex;
		}
		return rs;
	}

	public static String[] tryGetSubstrings(String source, Strings... analyzers) {
		return tryGetSubstrings(source, 0, analyzers);
	}

	public static String tryGetSubstring(String source, Strings analyzer, int fromIndex) {
		SubstringItem holder = analyzer.getSubstring(source, fromIndex);
		if (null == holder) {
			return null;
		}
		return holder.value;
	}

	public static String tryGetSubstring(String source, Strings analyzer) {
		return tryGetSubstring(source, analyzer, 0);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	/* get substring */
	public static String[] getSubstrings(String source, int fromIndex, Strings... analyzers) {
		String[] rs = new String[analyzers.length];
		int fi = fromIndex < 0 ? 0 : fromIndex;
		for (int i = 0; i < analyzers.length; i++) {
			if (fi < 0) {
				throw newSubstringException(analyzers[i], source);
			}
			SubstringItem holder = analyzers[i].getSubstring(source, fi);
			if (null == holder) {
				throw newSubstringException(analyzers[i], source);
			}
			rs[i] = holder.value;
			fi = holder.nextFromIndex;
		}
		return rs;
	}

	public static String[] getSubstrings(String source, Strings... analyzers) {
		return getSubstrings(source, 0, analyzers);
	}

	public static String getSubstring(String source, Strings analyzer, int fromIndex) {
		SubstringItem holder = analyzer.getSubstring(source, fromIndex);
		if (null == holder) {
			throw newSubstringException(analyzer, source);
		}
		return holder.value;
	}

	public static SubstringItem getSubstringItem(String source, Strings analyzer, int fromIndex) {
		return analyzer.getSubstring(source, fromIndex);
	}

	public static String getSubstring(String source, Strings analyzer) {
		return getSubstring(source, analyzer, 0);
	}

	public static SubstringItem getSubstringItem(String source, Strings analyzer) {
		return getSubstringItem(source, analyzer, 0);
	}

	/* get substrings */
	public static String[] getAllSubstrings(String source, Strings analyzer, int fromIndex, int count) {
		return analyzer.getSubstrings(source, fromIndex, count).values;
	}

	public static String[] getAllSubstringsFromIndex(String source, Strings analyzer, int fromIndex) {
		return getAllSubstrings(source, analyzer, fromIndex, 0);
	}

	public static String[] getAllSubstringsByCount(String source, Strings analyzer, int count) {
		return getAllSubstrings(source, analyzer, 0, count);
	}

	public static String[] getAllSubstrings(String source, Strings analyzer) {
		return getAllSubstrings(source, analyzer, 0, 0);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static boolean parseBool(String value) {
		return Boolean.parseBoolean(value);
	}

	public static boolean parseBool(String value, boolean nullValue) {
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return Boolean.parseBoolean(value);
	}

	public static boolean tryParseBool(String value, boolean defaultValue) {
		if (Strings.isNullOrEmpty(value)) {
			return defaultValue;
		}
		try {
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Boolean valueOfBool(String value) {
		return Boolean.valueOf(value);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static byte parseByte(String value) {
		return Byte.parseByte(value);
	}

	public static byte parseByte(String value, byte nullValue) {
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return Byte.parseByte(value);
	}

	public static byte tryParseByte(String value, byte defaultValue) {
		if (Strings.isNullOrEmpty(value)) {
			return defaultValue;
		}
		try {
			return Byte.parseByte(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Byte valueOfByte(String value) {
		return Byte.valueOf(value);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static double parseDouble(String value) {
		return Double.parseDouble(value);
	}

	public static double parseDouble(String value, double nullValue) {
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return Double.parseDouble(value);
	}

	public static double tryParseDouble(String value, double defaultValue) {
		if (Strings.isNullOrEmpty(value)) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Double valueOfDouble(String value) {
		return Double.valueOf(value);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static float parseFloat(String value) {
		return Float.parseFloat(value);
	}

	public static float parseFloat(String value, float nullValue) {
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return Float.parseFloat(value);
	}

	public static float tryParseFloat(String value, float defaultValue) {
		if (Strings.isNullOrEmpty(value)) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Float valueOfFloat(String value) {
		return Float.valueOf(value);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static int parseInt(String value) {
		return Integer.parseInt(value);
	}

	public static int parseInt(String value, int nullValue) {
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return Integer.parseInt(value);
	}

	public static int tryParseInt(String value, int defaultValue) {
		if (!Strings.isNumeric(value)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Integer valueOfInt(String value) {
		return Integer.valueOf(value);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static long parseLong(String value) {
		return Long.parseLong(value);
	}

	public static long parseLong(String value, long nullValue) {
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return Long.parseLong(value);
	}

	public static long tryParseLong(String value, long defaultValue) {
		if (!Strings.isNumeric(value)) {
			return defaultValue;
		}
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Long valueOfLong(String value) {
		return Long.valueOf(value);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static short parseShort(String value) {
		return Short.parseShort(value);
	}

	public static short parseShort(String value, short nullValue) {
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return Short.parseShort(value);
	}

	public static short tryParseShort(String value, short defaultValue) {
		if (!Strings.isNumeric(value)) {
			return defaultValue;
		}
		try {
			return Short.parseShort(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Short valueOfShort(String value) {
		return Short.valueOf(value);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static DateTime parseDateTime(String value) {
		return DateTime.valueOf(value);
	}

	public static DateTime parseDateTime(String value, DateTime nullValue) {
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return DateTime.valueOf(value);
	}

	public static DateTime tryParseDateTime(String value, DateTime defaultValue) {
		if (Strings.isNullOrEmpty(value)) {
			return defaultValue;
		}
		try {
			return DateTime.valueOf(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static DateTime valueOfDateTime(String value) {
		return parseDateTime(value, null);
	}

	// ////////////////////////////////////////////////
	// /
	// ////////////////////////////////////////////////
	public static Class<?> parseClass(String value) {
		try {
			return Class.forName(value);
		} catch (ClassNotFoundException e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static Class<?> parseClass(String value, Class<?> nullValue) {
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return parseClass(value);
	}

	public static Class<?> tryParseClass(String value, Class<?> defaultValue) {
		if (Strings.isNullOrEmpty(value)) {
			return defaultValue;
		}
		try {
			return Class.forName(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Class<?> valueOfClass(String value) {
		return parseClass(value);
	}

}
