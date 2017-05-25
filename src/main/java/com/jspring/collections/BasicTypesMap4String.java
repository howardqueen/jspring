package com.jspring.collections;

import com.jspring.Strings;
import com.jspring.date.DateTime;

public abstract class BasicTypesMap4String extends BasicTypesMap {

	@Override
	public final int getInt(String key) {
		return Strings.parseInt(getString(key));
	}

	@Override
	public final int getInt(String key, int nullValue) {
		return Strings.parseInt(getStringNullable(key), nullValue);
	}

	@Override
	public final boolean getBool(String key) {
		return Strings.parseBool(getString(key));
	}

	@Override
	public final boolean getBool(String key, boolean nullValue) {
		return Strings.parseBool(key, nullValue);
	}

	@Override
	public final long getLong(String key) {
		return Strings.parseLong(getString(key));
	}

	@Override
	public final long getLong(String key, long nullValue) {
		return Strings.parseLong(getStringNullable(key), nullValue);
	}

	@Override
	public final DateTime getDateTime(String key) {
		return Strings.parseDateTime(getString(key));
	}

	@Override
	public final DateTime getDateTime(String key, DateTime nullValue) {
		return Strings.parseDateTime(getStringNullable(key), nullValue);
	}

	@Override
	public final byte getByte(String key) {
		return Strings.parseByte(getString(key));
	}

	@Override
	public final byte getByte(String key, byte nullValue) {
		return Strings.parseByte(getStringNullable(key), nullValue);
	}

	@Override
	public final double getDouble(String key) {
		return Strings.parseDouble(getString(key));
	}

	@Override
	public final double getDouble(String key, double nullValue) {
		return Strings.parseDouble(getStringNullable(key), nullValue);
	}

	@Override
	public final float getFloat(String key) {
		return Strings.parseFloat(getString(key));
	}

	@Override
	public final float getFloat(String key, float nullValue) {
		return Strings.parseFloat(getStringNullable(key), nullValue);
	}

	@Override
	public final short getShort(String key) {
		return Strings.parseShort(getString(key));
	}

	@Override
	public final short getShort(String key, short nullValue) {
		return Strings.parseShort(getStringNullable(key), nullValue);
	}

	@Override
	public final Object get(String key) {
		return getStringNullable(key);
	}

	@Override
	public final Class<?> getClass(String key) {
		return Strings.parseClass(getString(key));
	}

	@Override
	public final Class<?> getClass(String key, Class<?> nullValue) {
		return Strings.parseClass(getStringNullable(key), nullValue);
	}

}
