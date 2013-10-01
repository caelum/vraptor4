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

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.singletonMap;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.serialization.Option;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

/**
 * A SerializerBuilder based on Gson.
 * 
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */
@RequestScoped
public class GsonSerializer
	implements SerializerBuilder {

	private final GsonBuilder builder = new GsonBuilder();
	private final Gson gson = builder.create();
	private Object object;
	private String alias;

	private HttpServletResponse response;
	private Instance<JsonSerializer<?>> serializers;

	@Inject
	public GsonSerializer(HttpServletResponse response, @Any Instance<JsonSerializer<?>> serializers) {
		this.response = response;
		this.serializers = serializers;
	}

	@Override
	public <T> Serializer from(T object) {
		return from(object, null);
	}

	@Override
	public <T> Serializer from(T object, String alias) {
		this.object = object;
		this.alias = alias;
		return this;
	}

	@Override
	public Serializer with(Option... options) {
		if (options != null) {
			for (Option opt : options) {
				opt.apply(gson);
			}
		}

		return this;
	}

	@Override
	public void serialize() {
		for (JsonSerializer<?> adapter : serializers) {
			builder.registerTypeHierarchyAdapter(getAdapterType(adapter), adapter);
		}

		if (isNullOrEmpty(alias)) {
			object = singletonMap(alias, object);
		}

		try (Writer writter = response.getWriter()) {
			gson.toJson(object, writter);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private Class<?> getAdapterType(JsonSerializer<?> adapter) {
		Type[] genericInterfaces = adapter.getClass().getGenericInterfaces();
		ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		return (Class<?>) type.getActualTypeArguments()[0];
	}
}