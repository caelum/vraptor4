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
import com.google.gson.JsonSerializer;

/**
 * Builder for JSON using GSON.
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */
@Dependent
public class GsonSerializerBuilder {

	private final Serializee serializee = new Serializee();
	private final Instance<JsonSerializer<?>> serializers;
	
	private GsonBuilder builder = new GsonBuilder();
	private List<ExclusionStrategy> exclusions;
	private boolean withoutRoot;
	private String alias;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected GsonSerializerBuilder() {
		this(null);
	}

	@Inject
	public GsonSerializerBuilder(@Any Instance<JsonSerializer<?>> serializers) {
		this.serializers = serializers;
		ExclusionStrategy exclusion = new Exclusions(serializee);
		exclusions = singletonList(exclusion);
	}

	public boolean isWithoutRoot() {
		return withoutRoot;
	}

	public void setWithoutRoot(boolean withoutRoot) {
		this.withoutRoot = withoutRoot;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void indented() {
		builder.setPrettyPrinting();
	}

	public void setExclusionStrategies(ExclusionStrategy... strategies) {
		builder.setExclusionStrategies(strategies);
	}

	public Gson create() {
		for (JsonSerializer<?> adapter : serializers) {
			RegisterStrategy registerStrategy = adapter.getClass().getAnnotation(RegisterStrategy.class);
			if ((registerStrategy != null) && (registerStrategy.value() == RegisterType.SINGLE))
				builder.registerTypeAdapter(getAdapterType(adapter), adapter);
			else
				builder.registerTypeHierarchyAdapter(getAdapterType(adapter), adapter);
		}

		for (ExclusionStrategy exclusion : exclusions) {
			builder.addSerializationExclusionStrategy(exclusion);
		}

		return builder.create();
	}

	private Class<?> getAdapterType(JsonSerializer<?> adapter) {
		Type[] genericInterfaces = adapter.getClass().getGenericInterfaces();
		ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		Type actualType = type.getActualTypeArguments()[0];
		
		if (actualType instanceof ParameterizedType)
			return (Class<?>) ((ParameterizedType) actualType).getRawType();
		else	
			return (Class<?>) actualType;
	}
	
	public Serializee getSerializee() {
		return serializee;
	}
}