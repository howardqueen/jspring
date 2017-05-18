package com.jspring.collections;

/**
 * The dictionary with Key:Int, Value:V
 * 
 * @author hqian
 * 
 * @param <V>
 */
public abstract class ArraySearch<V> {

	private final int offset;
	private final V[] values;

	public ArraySearch(int minIndex, int maxIndex) {
		offset = minIndex;
		values = newValues(maxIndex - minIndex + 1);
	}

	protected abstract V[] newValues(int length);

	public void set(int key, V value) {
		values[key - offset] = value;
	}

	public boolean trySet(int key, V value) {
		int i = key - offset;
		if (i >= values.length || i < 0) {
			return false;
		}
		values[i] = value;
		return true;
	}

	public V get(int key) {
		return values[key - offset];
	}

	public V tryGet(int key) {
		int i = key - offset;
		if (i >= values.length || i < 0) {
			return null;
		}
		return values[i];
	}

	public void enumRead(IEnumReader<V> reader) {
		for (int key = 0; key < values.length; key++) {
			reader.read(key + offset, values[key - offset]);
		}
	}

	public static interface IEnumReader<V> {
		public void read(int key, V value);
	}

	/**
	 * The dictionary with Key:Int, Value:Byte
	 * 
	 * @author hqian
	 * 
	 */
	public static class ByteArraySearch {

		private final int offset;
		private final byte[] values;

		public ByteArraySearch(int minValue, int maxValue) {
			offset = minValue;
			values = new byte[maxValue - minValue + 1];
		}

		public void set(int key, byte value) {
			values[key - offset] = value;
		}

		public boolean trySet(int key, byte value) {
			int i = key - offset;
			if (i >= values.length || i < 0) {
				return false;
			}
			values[i] = value;
			return true;
		}

		public byte get(int key) {
			return values[key - offset];
		}

		public byte tryGet(int key, byte defaultValue) {
			int i = key - offset;
			if (i >= values.length || i < 0) {
				return defaultValue;
			}
			return values[i];
		}

		public void enumRead(IEnumReader reader) {
			for (int key = 0; key < values.length; key++) {
				reader.read(key + offset, values[key - offset]);
			}
		}

		public static interface IEnumReader {
			public void read(int key, byte value);
		}

	}

	/**
	 * The dictionary with Key:Int, Value:Int
	 * 
	 * @author hqian
	 * 
	 */
	public static class IntArraySearch {

		private final int offset;
		private final int[] values;

		public IntArraySearch(int minValue, int maxValue) {
			offset = minValue;
			values = new int[maxValue - minValue + 1];
		}

		public void set(int key, int value) {
			values[key - offset] = value;
		}

		public boolean trySet(int key, int value) {
			int i = key - offset;
			if (i >= values.length || i < 0) {
				return false;
			}
			values[i] = value;
			return true;
		}

		public int get(int key) {
			return values[key - offset];
		}

		public int tryGet(int key, int defaultValue) {
			int i = key - offset;
			if (i >= values.length || i < 0) {
				return defaultValue;
			}
			return values[i];
		}

		public void enumRead(IEnumReader reader) {
			for (int key = 0; key < values.length; key++) {
				reader.read(key + offset, values[key - offset]);
			}
		}

		public static interface IEnumReader {
			public void read(int key, int value);
		}

	}
}
