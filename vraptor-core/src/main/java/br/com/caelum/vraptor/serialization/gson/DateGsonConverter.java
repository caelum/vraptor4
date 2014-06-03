package br.com.caelum.vraptor.serialization.gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.Dependent;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

/**
 * Deserialize {@link Date} using ISO8601 format. 
 * This class must be in {@link Dependent} to allow us to discover generic type.
 * 
 * @author Rodrigo Turini
 * @since 4.0.0
 */
@Dependent
public class DateGsonConverter implements JsonDeserializer<Date>, JsonSerializer<Date>{

	private final SimpleDateFormat iso8601Format;

	public DateGsonConverter() {
		this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	}
	
	@Override
	public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {
		String dateString = iso8601Format.format(date);
		return new JsonPrimitive(dateString);
	}

	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		try {
			return iso8601Format.parse(json.getAsString());
		} catch (ParseException e) {
			throw new JsonSyntaxException(json.getAsString(), e);
		}
	}

}