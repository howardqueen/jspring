package com.jspring;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

public final class Reflects {
	private Reflects() {
	}

	/* Arrays */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(T[] empty, int length) {
		return (T[]) Array.newInstance(empty.getClass(), length);
	}

	public static <T> T[] subArray(T[] source, int startIndex, int endIndex) {
		return Arrays.copyOfRange(source, startIndex, endIndex);
	}

	//@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> T[] join(T[]... arrays) {
		int i = 0;
		for (T[] a : arrays) {
			i += a.length;
		}
		T[] r = newArray(arrays[0], i);
		i = 0;
		for (T[] a : arrays) {
			System.arraycopy(a, 0, r, i, a.length);
			i += a.length;
		}
		return r;
	}

	public static <T> T[] join(Collection<T[]> arrays, T[] empty) {
		int i = 0;
		for (T[] a : arrays) {
			i += a.length;
		}
		T[] r = arrays.toArray(empty);
		i = 0;
		for (T[] a : arrays) {
			System.arraycopy(a, 0, r, i, a.length);
			i += a.length;
		}
		return r;
	}

	//@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> T[] join(T[] source, T... items) {
		T[] r = newArray(source, source.length + items.length);
		for (int i = source.length; i < r.length; i++) {
			r[i] = items[i - source.length];
		}
		return r;
	}

	public static int[] joinInts(Collection<int[]> arrays) {
		int i = 0;
		for (int[] a : arrays) {
			i += a.length;
		}
		int[] r = new int[i];
		i = 0;
		for (int[] a : arrays) {
			System.arraycopy(a, 0, r, i, a.length);
			i += a.length;
		}
		return r;
	}

	public static Object construct(Class<?> cls, Object args) {
		try {
			Constructor<?> constructor = cls.getConstructor(args.getClass());
			return constructor.newInstance(args);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static <T> T construct(String className, Object args) {
		try {
			@SuppressWarnings("unchecked")
			Constructor<T> constructor = (Constructor<T>) Class.forName(className).getConstructor(args.getClass());
			return constructor.newInstance(args);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static <T> T construct(Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static Object construct(String className) {
		try {
			return Class.forName(className).newInstance();
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

}
