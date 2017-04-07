package com.jspring.text;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jspring.Exceptions;
import com.jspring.date.DateTime;

/**
 * @author hqian
 * 
 */
public final class JsonSerializer {

	private JsonSerializer() {
	}

	private static final Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeAdapter())
			.create();

	public static String serializeNonGeneric(Object t) {
		return gson.toJson(t);
	}

	public static <T> ISerializer<T> newNonGenericSerializer(Class<T> tClass) {
		return new t<T>(tClass);
	}

	public static <T> ISerializer<T> newGenericSerializer(TypeToken<T> tTypeToken) {
		return new t2<T>(tTypeToken.getType());
	}

	private static class t<T> implements ISerializer<T> {

		private final Class<T> classOfT;

		public t(Class<T> classOfT) {
			this.classOfT = classOfT;
		}

		@Override
		public String serialize(T t) {
			return gson.toJson(t);
		}

		@Override
		public T deserialize(String value) {
			try {
				return gson.fromJson(value, classOfT);
			} catch (Exception e) {
				throw Exceptions.newInstance("Deserialize to json failed: "
						+ (value.length() > 20 ? value.substring(0, 20) + "..." : value));
			}
		}
	}

	private static class t2<T> implements ISerializer<T> {
		private final Type genericType;

		public t2(Type genericType) {
			this.genericType = genericType;
		}

		@Override
		public String serialize(T t) {
			return gson.toJson(t, this.genericType);
		}

		@Override
		public T deserialize(String value) {
			try {
				return gson.fromJson(value, this.genericType);
			} catch (Exception e) {
				throw Exceptions.newInstance("Deserialize to json failed: "
						+ (value.length() > 20 ? value.substring(0, 20) + "..." : value));
			}
		}
	}

}
