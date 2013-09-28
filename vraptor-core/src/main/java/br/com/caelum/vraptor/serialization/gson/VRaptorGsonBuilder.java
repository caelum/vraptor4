package br.com.caelum.vraptor.serialization.gson;

import static java.util.Collections.singletonList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
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
public class VRaptorGsonBuilder {

	private boolean withoutRoot;
	private String alias;

	private GsonBuilder builder = new GsonBuilder();
	private Collection<JsonSerializer> serializers;
	private Collection<ExclusionStrategy> exclusions;
	private Serializee serializee;

	@Deprecated
	public VRaptorGsonBuilder() {
	}

	@Inject
	public VRaptorGsonBuilder(List<JsonSerializer> serializers, Serializee serializee) {
		this.serializers = serializers;
		this.serializee = serializee;
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

		return (Class<?>) actualType;
	}
	
	public Serializee getSerializee() {
		return serializee;
	}
}