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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.Serializer;

/**
 * Gson implementation for JSONSerialization
 *
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */
@RequestScoped
public class GsonJSONSerialization implements JSONSerialization {

	private HttpServletResponse response;
	private GsonSerializer serializer;

	@Deprecated
	public GsonJSONSerialization() {
	}

	@Inject
	public GsonJSONSerialization(GsonSerializer serializer, HttpServletResponse response) {
		this.serializer = serializer;
		this.response = response;
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
		return serializer.from(object, alias);
	}
}