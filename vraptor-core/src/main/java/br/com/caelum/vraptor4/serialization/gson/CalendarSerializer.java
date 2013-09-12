package br.com.caelum.vraptor4.serialization.gson;

import java.lang.reflect.Type;
import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serialize {@link Calendar} using ISO8601 format.
 * 
 * @author Renan Reis
 * @author Otávio Garcia
 */
public class CalendarSerializer implements JsonSerializer<Calendar> {

	public JsonElement serialize(Calendar calendar, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(DatatypeConverter.printDateTime(calendar));
	}
}