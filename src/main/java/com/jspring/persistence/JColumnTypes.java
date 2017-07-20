package com.jspring.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jspring.Strings;

public enum JColumnTypes {

	Unknown, String, Integer, Long, Short, Double, Float, Boolean, DateTime, Date;

	public static JColumnTypes of(Field field) {
		if (Modifier.isStatic(field.getModifiers())) {
			return JColumnTypes.Unknown;
		}
		switch (field.getType().getSimpleName()) {
		case "Date":
			JsonFormat jf = field.getType().getAnnotation(JsonFormat.class);
			if (null != jf && !Strings.isNullOrEmpty(jf.pattern()) && jf.pattern().equals("yyyy-MM-dd")) {
				return Date;
			}
			return DateTime;
		case "String":
			return String;
		case "Integer":
			return Integer;
		case "Long":
			return Long;
		case "Short":
			return Short;
		case "Double":
			return Double;
		case "Float":
			return Float;
		case "Boolean":
			return Boolean;
		default:
			return Unknown;
		}
	}

}
