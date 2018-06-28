package br.com.caelum.vraptor.serialization.gson;

import com.google.gson.*;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;
import java.lang.reflect.Type;

@RegisterStrategy(RegisterType.SINGLE)
@Dependent
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class BeanSerializer implements JsonSerializer<Bean> {
	private static final JsonObject element = new JsonObject();
	static{
		element.add("test123", new JsonObject());
	}

	@Override
	public JsonElement serialize(Bean src, Type typeOfSrc, JsonSerializationContext context) {
		return element;
	}
}
