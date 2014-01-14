package br.com.caelum.vraptor.serialization.gson;

import static java.util.Collections.singletonList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.serialization.Serializee;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * Builder Wrapper for JSON using GSON.
 * 
 * @author Rafael Dipold
 */
@Dependent
public class GsonBuilderWrapper implements GsonSerializerBuilder, GsonDeserializerBuilder {
	
	private GsonBuilder builder = new GsonBuilder();
	private Serializee serializee = new Serializee();
	private boolean withoutRoot;
	private String alias;
	private List<ExclusionStrategy> exclusions;
	
	private final Iterable<JsonSerializer<?>> jsonSerializers;
	private final Iterable<JsonDeserializer<?>> jsonDeserializers;
	
	@Inject
	public GsonBuilderWrapper(@Any Instance<JsonSerializer<?>> jsonSerializers, @Any Instance<JsonDeserializer<?>> jsonDeserializers) {
		this.jsonSerializers = jsonSerializers;
		this.jsonDeserializers = jsonDeserializers;
		ExclusionStrategy exclusion = new Exclusions(serializee);
		exclusions = singletonList(exclusion);
	}

	/**
	 * @deprecated CDI eyes only
	 */
	protected GsonBuilderWrapper() {
		this(null, null);
	}
	@Override
	public Gson create() {
		for (JsonSerializer<?> adapter : jsonSerializers) {
			registerAdapter(getAdapterType(adapter), adapter);
		}

		for (JsonDeserializer<?> adapter : jsonDeserializers) {
			registerAdapter(getAdapterType(adapter), adapter);
		}
		
		for (ExclusionStrategy exclusion : exclusions) {
			builder.addSerializationExclusionStrategy(exclusion);
		}
		
		return builder.create();
	}
	
	private void registerAdapter(Class<?> adapterType, Object adapter) {
		RegisterStrategy registerStrategy = adapter.getClass().getAnnotation(RegisterStrategy.class);
		if ((registerStrategy != null) && (registerStrategy.value().equals(RegisterType.SINGLE))) {
			builder.registerTypeAdapter(adapterType, adapter);
		} else {
			builder.registerTypeHierarchyAdapter(adapterType, adapter);
		}	
	}
	
	private Class<?> getAdapterType(Object adapter) {
		Type[] genericInterfaces = adapter.getClass().getGenericInterfaces();
		ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		Type actualType = type.getActualTypeArguments()[0];

		if (actualType instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) actualType).getRawType();
		} else {
			return (Class<?>) actualType;
		}
	}

	@Override
	public Serializee getSerializee() {
		return serializee;
	}

	@Override
	public boolean isWithoutRoot() {
		return withoutRoot;
	}

	@Override
	public void setWithoutRoot(boolean withoutRoot) {
		this.withoutRoot = withoutRoot;		
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public void indented() {
		builder.setPrettyPrinting();
	}

	@Override
	public void setExclusionStrategies(ExclusionStrategy... strategies) {
		builder.setExclusionStrategies(strategies);
	}
}
