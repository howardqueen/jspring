package com.jspring.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.jspring.date.DateTime;

import java.lang.reflect.Type;

public class DateTimeAdapter
		implements com.google.gson.JsonSerializer<DateTime>, com.google.gson.JsonDeserializer<DateTime> {

	@Override
	public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return null == json ? null : DateTime.valueOfDateTime(json.getAsString());
	}

	@Override
	public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
		return null == src ? new JsonPrimitive("") : new JsonPrimitive(src.toDateTimeString());
	}

}
