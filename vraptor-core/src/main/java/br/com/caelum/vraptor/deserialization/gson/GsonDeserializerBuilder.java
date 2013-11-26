package br.com.caelum.vraptor.deserialization.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

/**
 * Builder for JSON using GSON.
 * 
 * @author Rafael Dipold
 */
@Dependent
public class GsonDeserializerBuilder {

	private final Instance<JsonDeserializer<?>> adapters;
	private GsonBuilder builder = new GsonBuilder();

	@Inject
	public GsonDeserializerBuilder(@Any Instance<JsonDeserializer<?>> adapters) {
		this.adapters = adapters;
	}

	/**
	 * @deprecated CDI eyes only
	 */
	protected GsonDeserializerBuilder() {
		this(null);
	}

	public Gson create() {
		for (JsonDeserializer<?> adapter : adapters) {
			builder.registerTypeHierarchyAdapter(getAdapterType(adapter), adapter);
		}

		return builder.create();
	}

	private Class<?> getAdapterType(JsonDeserializer<?> adapter) {
		Type[] genericInterfaces = adapter.getClass().getGenericInterfaces();
		ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		Type actualType = type.getActualTypeArguments()[0];

		if (actualType instanceof ParameterizedType)
			return (Class<?>) ((ParameterizedType) actualType).getRawType();
		else
			return (Class<?>) actualType;
	}
}