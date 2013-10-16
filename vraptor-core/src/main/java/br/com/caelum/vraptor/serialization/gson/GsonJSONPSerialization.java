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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.JSONPSerialization;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

/**
 * Gson implementation for JSONPSerialization
 *
 * @author Otávio Scherer Garcia
 * @since 4.0.0
 */
@RequestScoped
public class GsonJSONPSerialization implements JSONPSerialization {
	
	private final HttpServletResponse response;
	private final TypeNameExtractor extractor;
	private final VRaptorGsonBuilder builder;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected GsonJSONPSerialization() {
		this(null, null, null);
	}

	@Inject
	public GsonJSONPSerialization(HttpServletResponse response, TypeNameExtractor extractor,
			VRaptorGsonBuilder builder) {
		this.response = response;
		this.extractor = extractor;
		this.builder = builder;
	}
	
	@Override
	public JSONSerialization withCallback(final String callbackName) {
		return new GsonJSONSerialization(response, extractor, builder) {
			@Override
			protected SerializerBuilder getSerializer() {
				try {
					return new GsonSerializer(builder, response.getWriter(), extractor) {
						@Override
						public void serialize() {
							try {
								response.getWriter().append(callbackName).append("(");
								super.serialize();
								response.getWriter().append(")");
								response.getWriter().flush();
							} catch (IOException e) {
								throw new ResultException("Unable to serialize data", e);
							}
						}
					};
				} catch (IOException e) {
					throw new ResultException("Unable to serialize data", e);
				}
			}
		};
	}

}