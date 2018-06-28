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

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.charset.Charset;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.serialization.Deserializee;
import br.com.caelum.vraptor.serialization.Deserializer;
import br.com.caelum.vraptor.serialization.DeserializerConfig;
import br.com.caelum.vraptor.serialization.Deserializes;
import br.com.caelum.vraptor.view.ResultException;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A GSON deserializer.
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */

@Deserializes({ "application/json", "json" })
public class GsonDeserialization implements Deserializer {

	private static final Logger logger = LoggerFactory.getLogger(GsonDeserialization.class);

	private final GsonDeserializerBuilder builder;
	private final ParameterNameProvider paramNameProvider;
	private final HttpServletRequest request;
	private final Container container;
	private final Instance<Deserializee> deserializeeInstance;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected GsonDeserialization() {
		this(null, null, null, null, null);
	}
	
	@Inject
	public GsonDeserialization(GsonDeserializerBuilder builder, ParameterNameProvider paramNameProvider, HttpServletRequest request, 
			Container container, Instance<Deserializee> deserializeeInstance) {
		this.builder = builder;
		this.paramNameProvider = paramNameProvider;
		this.request = request;
		this.container = container;
		this.deserializeeInstance = deserializeeInstance;
	}

	@Override
	public Object[] deserialize(InputStream inputStream, ControllerMethod method) {
		Class<?>[] types = getTypes(method);
		
		if (types.length == 0) {
			throw new IllegalArgumentException("Methods that consumes representations must receive just one argument");
		}

		Gson gson = builder.create();
		
		final Parameter[] parameterNames = paramNameProvider.parametersFor(method.getMethod());
		final Object[] values = new Object[parameterNames.length];
		final Deserializee deserializee = deserializeeInstance.get();

		try {
			String content = getContentOfStream(inputStream);
			logger.debug("json retrieved: {}", content);
			
			if (!isNullOrEmpty(content)) {
				JsonParser parser = new JsonParser();
				JsonElement jsonElement = parser.parse(content);
				if (jsonElement.isJsonObject()) {
					JsonObject root = jsonElement.getAsJsonObject();
		
					deserializee.setWithoutRoot(isWithoutRoot(parameterNames, root));
					
					for(Class<? extends DeserializerConfig> option: method.getMethod().getAnnotation(Consumes.class).options()) {
						DeserializerConfig config = container.instanceFor(option);
						config.config(deserializee);
					}
					
					for (int i = 0; i < types.length; i++) {
						Parameter parameter = parameterNames[i];
						JsonElement node = root.get(parameter.getName());
						
						if (deserializee.isWithoutRoot()) { 
							values[i] = gson.fromJson(root, fallbackTo(parameter.getParameterizedType(), types[i]));
							logger.debug("json without root deserialized");
							break;

						} else if (node != null) {
							if (node.isJsonArray()) {
								JsonArray jsonArray= node.getAsJsonArray();
								Type type = parameter.getParameterizedType();
								if (type instanceof ParameterizedType) {
									values[i] = gson.fromJson(jsonArray, type);
								} else {
									values[i] = gson.fromJson(jsonArray, types[i]);
								}
							} else {
								values[i] = gson.fromJson(node, types[i]);
							}
						}
					}
				} else if (jsonElement.isJsonArray()) {
					if ((parameterNames.length != 1) || (!(parameterNames[0].getParameterizedType() instanceof ParameterizedType)))
						throw new IllegalArgumentException("Methods that consumes an array representation must receive only just one collection generic typed argument");

					JsonArray jsonArray= jsonElement.getAsJsonArray();
					values[0] = gson.fromJson(jsonArray, parameterNames[0].getParameterizedType());
				} else {
					throw new IllegalArgumentException("This is an invalid or not supported json content");
				}
			}
		} catch (Exception e) {
			throw new ResultException("Unable to deserialize data", e);
		}

		logger.debug("json deserialized: {}", (Object) values);
		return values;
	}

	private static Type fallbackTo(Type parameterizedType, Class<?> type) {
		if (parameterizedType instanceof TypeVariable) return type;
		return parameterizedType;
	}

	private String getContentOfStream(InputStream input) throws IOException {
		Charset charset = Charset.forName(getRequestCharset());
		logger.debug("Using charset {}", charset);

		return CharStreams.toString(new InputStreamReader(input, charset));
	}

	private String getRequestCharset() {
		String charset = firstNonNull(request.getHeader("Accept-Charset"), "UTF-8");
		return charset.split(",")[0];
	}

	private static boolean isWithoutRoot(Parameter[] parameters, JsonObject root) {
		for (Parameter parameter : parameters) {
			if (root.get(parameter.getName()) != null)
				return false;
		}
		return true;
	}

	protected Class<?>[] getTypes(ControllerMethod method) {
		Class<?>[] parameterTypes = method.getMethod().getParameterTypes();
		Type genericType = getGenericSuperClass(method);
		if (genericType != null) {
			return parseGenericParameters(parameterTypes, genericType);
		}

		return parameterTypes;
	}

	private static Class<?>[] parseGenericParameters(Class<?>[] parameterTypes,
			Type genericType) {
		Class<?> type = (Class<?>) getGenericType(genericType);
		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i].isAssignableFrom(type)) {
				parameterTypes[i] = type;
			}
		}
		return parameterTypes;
	}

	private static Type getGenericSuperClass(ControllerMethod method) {
		Type genericType = method.getController().getType().getGenericSuperclass();
		if (genericType instanceof ParameterizedType) {
			return genericType;
		}

		return null;
	}

	private static Type getGenericType(Type type) {
		ParameterizedType paramType = (ParameterizedType) type;
		return paramType.getActualTypeArguments()[0];
	}
}
