package br.com.caelum.vraptor.serialization.gson;

import java.lang.reflect.Type;
import java.util.Calendar;

import javax.enterprise.context.Dependent;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Deserialize {@link Calendar} using ISO8601 format. This class must be in {@link Dependent} to allow us to
 * discover generic type.
 * 
 * @author Otávio Garcia
 * @since 4.0.0
 */
@Dependent
public class CalendarGsonConverter implements JsonDeserializer<Calendar>, JsonSerializer<Calendar>{

	@Override
	public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return DatatypeConverter.parseDate(json.getAsString());
	}

	@Override
	public JsonElement serialize(Calendar calendar, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(DatatypeConverter.printDateTime(calendar));
	}
}