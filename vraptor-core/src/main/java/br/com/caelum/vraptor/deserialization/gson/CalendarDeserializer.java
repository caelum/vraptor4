package br.com.caelum.vraptor.deserialization.gson;

import java.lang.reflect.Type;
import java.util.Calendar;

import javax.enterprise.context.Dependent;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Deserialize {@link Calendar} using ISO8601 format. This class must be in {@link Dependent} to allow us to
 * discover generic type.
 * 
 * @author Otávio Garcia
 * @since 4.0.0
 */
@Dependent
public class CalendarDeserializer implements JsonDeserializer<Calendar> {

	@Override
	public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return DatatypeConverter.parseDate(json.getAsString());
	}
}