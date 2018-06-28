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

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

/**
 * Gson implementation for JSONSerialization
 *
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */
@RequestScoped
public class GsonJSONSerialization implements JSONSerialization {

	private final HttpServletResponse response;
	private final TypeNameExtractor extractor;
	private final GsonSerializerBuilder builder;
	private Environment environment;
	private ReflectionProvider reflectionProvider;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected GsonJSONSerialization() {
		this(null, null, null, null, null);
	}

	@Inject
	public GsonJSONSerialization(HttpServletResponse response, TypeNameExtractor extractor,
			GsonSerializerBuilder builder, Environment environment, ReflectionProvider reflectionProvider) {
		this.response = response;
		this.extractor = extractor;
		this.builder = builder;
		this.environment = environment;
		this.reflectionProvider = reflectionProvider;
	}

	@PostConstruct
	protected void init() {
		if (environment.supports(ENVIRONMENT_INDENTED_KEY)) {
			builder.indented();
		}
	}

	@Override
	public boolean accepts(String format) {
		return "json".equals(format);
	}

	@Override
	public <T> Serializer from(T object) {
		return from(object, null);
	}

	@Override
	public <T> Serializer from(T object, String alias) {
		response.setContentType("application/json");
		return getSerializer().from(object, alias);
	}

	protected SerializerBuilder getSerializer() {
		try {
			return new GsonSerializer(builder, response.getWriter(), extractor, reflectionProvider);
		} catch (IOException e) {
			throw new ResultException("Unable to serialize data", e);
		}
	}

	/**
	 * You can override this method for configuring Driver before serialization
	 */
	@Override
	public NoRootSerialization withoutRoot() {
		builder.setWithoutRoot(true);
		return this;
	}

	@Override
	public JSONSerialization indented() {
		builder.indented();
		return this;
	}

	@Override
	public JSONSerialization version(double versionNumber) {
		builder.version(versionNumber);
		return this;
	}

	@Override
	public JSONSerialization serializeNulls() {
		builder.serializeNulls();
		return this;		
	}

}
