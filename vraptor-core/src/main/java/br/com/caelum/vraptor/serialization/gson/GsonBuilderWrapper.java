/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.serialization.gson;

import static br.com.caelum.vraptor.proxy.CDIProxies.extractRawTypeIfPossible;
import static java.util.Collections.singletonList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.serialization.Serializee;

/**
 * Builder Wrapper for JSON using GSON.
 * 
 * @author Rafael Dipold
 */
@Dependent
public class GsonBuilderWrapper implements GsonSerializerBuilder, GsonDeserializerBuilder {
	
	private final GsonBuilder builder = new GsonBuilder();
	private boolean withoutRoot;
	private String alias;
	private final List<ExclusionStrategy> exclusions;
	
	private final Serializee serializee;
	private final Iterable<JsonSerializer<?>> jsonSerializers;
	private final Iterable<JsonDeserializer<?>> jsonDeserializers;

	@Inject
	public GsonBuilderWrapper(@Any Instance<JsonSerializer<?>> jsonSerializers, 
			@Any Instance<JsonDeserializer<?>> jsonDeserializers,
			Serializee serializee, ReflectionProvider reflectionProvider) {
		this.jsonSerializers = jsonSerializers;
		this.jsonDeserializers = jsonDeserializers;
		this.serializee = serializee;
		ExclusionStrategy exclusion = new Exclusions(serializee, reflectionProvider);
		exclusions = singletonList(exclusion);
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
			getGsonBuilder().addSerializationExclusionStrategy(exclusion);
		}
		
		return getGsonBuilder().create();
	}
	
	protected void registerAdapter(Class<?> adapterType, Object adapter) {
		RegisterStrategy registerStrategy = adapter.getClass().getAnnotation(RegisterStrategy.class);
		if ((registerStrategy != null) && (registerStrategy.value().equals(RegisterType.SINGLE))) {
			getGsonBuilder().registerTypeAdapter(adapterType, adapter);
		} else {
			getGsonBuilder().registerTypeHierarchyAdapter(adapterType, adapter);
		}	
	}
	
	private Class<?> getAdapterType(Object adapter) {
		final Class<?> klazz = extractRawTypeIfPossible(adapter.getClass());
		final Type[] genericInterfaces = klazz.getGenericInterfaces();
		final ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		final Type actualType = type.getActualTypeArguments()[0];

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
		getGsonBuilder().setPrettyPrinting();
	}

	@Override
	public void setExclusionStrategies(ExclusionStrategy... strategies) {
		getGsonBuilder().setExclusionStrategies(strategies);
	}

	protected GsonBuilder getGsonBuilder() {
		return builder;
	}

	@Override
	public void version(double versionNumber) {
		getGsonBuilder().setVersion(versionNumber);
	}

	@Override
	public void serializeNulls() {
		getGsonBuilder().serializeNulls();
	}

}
