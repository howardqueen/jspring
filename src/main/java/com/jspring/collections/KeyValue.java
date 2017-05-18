package com.jspring.collections;

public class KeyValue<K, V> {
	public final K key;
	public V value;

	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public KeyValue(K key) {
		this.key = key;
	}
}
