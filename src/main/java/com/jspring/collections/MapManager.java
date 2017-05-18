package com.jspring.collections;

import java.util.*;

public class MapManager {

	private MapManager() {
	}

	public static <K, V> Map<K, V> newInstance() {
		return new HashMap<K, V>();
	}

	public static <K, V> Map<K, V> newInstance4ThreadSafe() {
		return new Hashtable<K, V>();
	}

	public static <K, V> Map<K, V> newBinarySearch() {
		return new TreeMap<K, V>();
	}

}
