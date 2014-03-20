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
package br.com.caelum.vraptor.serialization;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.validator.Message;

/**
 * @author Celso Dantas
 */
@Deserializes("application/x-www-form-urlencoded")
public class FormDeserializer implements Deserializer {

	private final ParametersProvider provider;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected FormDeserializer() {
		this(null);
	}
	
	@Inject
	public FormDeserializer(ParametersProvider provider) {
		this.provider = provider;
	}

	@Override
	public Object[] deserialize(InputStream inputStream, ControllerMethod method) {
		List<Message> errors = new ArrayList<>();
		return provider.getParametersFor(method, errors);
	}
}
