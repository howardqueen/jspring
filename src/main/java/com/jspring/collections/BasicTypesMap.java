package com.jspring.collections;

import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.date.DateTime;

public abstract class BasicTypesMap {

	// /////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////
	public final String getString(String key) {
		String value = getStringNullable(key);
		if (Strings.isNullOrEmpty(value)) {
			throw Exceptions.newNullArgumentException(key);
		}
		return value;
	}

	public final String getString(String key, String nullValue) {
		String value = getStringNullable(key);
		if (Strings.isNullOrEmpty(value)) {
			return nullValue;
		}
		return value;
	}

	public abstract String getStringNullable(String key);

	public abstract Object get(String key);

	public abstract boolean getBool(String key);

	public abstract boolean getBool(String key, boolean nullValue);

	public abstract byte getByte(String key);

	public abstract byte getByte(String key, byte nullValue);

	public abstract double getDouble(String key);

	public abstract double getDouble(String key, double nullValue);

	public abstract float getFloat(String key);

	public abstract float getFloat(String key, float nullValue);

	public abstract int getInt(String key);

	public abstract int getInt(String key, int nullValue);

	public abstract long getLong(String key);

	public abstract long getLong(String key, long nullValue);

	public abstract short getShort(String key);

	public abstract short getShort(String key, short nullValue);

	public abstract DateTime getDateTime(String key);

	public abstract DateTime getDateTime(String key, DateTime nullValue);

	public abstract Class<?> getClass(String key);

	public abstract Class<?> getClass(String key, Class<?> nullValue);

	// /////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////
	private String arraySplit;

	public final void setArraySplit(String value) {
		arraySplit = value;
	}

	public final String getArraySplit() {
		if (null == arraySplit) {
			arraySplit = getString("array.split", ",");
		}
		return arraySplit;
	}

	public final String[] getStrings(String key) {
		String value = getStringNullable(key);
		if (Strings.isNullOrEmpty(value)) {
			return null;
		}
		return value.split(getArraySplit());
	}

	public final int[] getInts(String key) {
		String[] t = getStrings(key);
		if (null == t) {
			return null;
		}
		int[] r = new int[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = Strings.parseInt(t[i]);
		}
		return r;
	}

	public final boolean[] getBools(String key) {
		String[] t = getStrings(key);
		if (null == t) {
			return null;
		}
		boolean[] r = new boolean[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = Strings.parseBool(t[i]);
		}
		return r;
	}

	public final long[] getLongs(String key) {
		String[] t = getStrings(key);
		if (null == t) {
			return null;
		}
		long[] r = new long[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = Strings.parseLong(t[i]);
		}
		return r;
	}

	public final byte[] getBytes(String key) {
		String[] t = getStrings(key);
		if (null == t) {
			return null;
		}
		byte[] r = new byte[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = Strings.parseByte(t[i]);
		}
		return r;
	}

	public final double[] getDoubles(String key) {
		String[] t = getStrings(key);
		if (null == t) {
			return null;
		}
		double[] r = new double[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = Strings.parseDouble(t[i]);
		}
		return r;
	}

	public final float[] getFloats(String key) {
		String[] t = getStrings(key);
		if (null == t) {
			return null;
		}
		float[] r = new float[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = Strings.parseFloat(t[i]);
		}
		return r;
	}

	public final short[] getShorts(String key) {
		String[] t = getStrings(key);
		if (null == t) {
			return null;
		}
		short[] r = new short[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = Strings.parseShort(t[i]);
		}
		return r;
	}

	public final DateTime[] getDateTimes(String key) {
		String[] t = getStrings(key);
		if (null == t) {
			return null;
		}
		DateTime[] r = new DateTime[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = Strings.parseDateTime(t[i]);
		}
		return r;
	}

	public final Class<?>[] getClasses(String key) {
		String[] t = getStrings(key);
		if (null == t) {
			return null;
		}
		Class<?>[] r = new Class<?>[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = Strings.parseClass(t[i]);
		}
		return r;
	}

}
