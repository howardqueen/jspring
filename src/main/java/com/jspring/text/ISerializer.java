package com.jspring.text;

public interface ISerializer<T> {
    String serialize(T t);

    T deserialize(String value);
}
