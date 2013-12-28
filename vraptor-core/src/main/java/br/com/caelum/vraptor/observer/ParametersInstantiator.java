/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.observer;

import static com.google.common.base.Preconditions.checkArgument;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.com.caelum.vraptor.HeaderParam;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.StackStarting;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.Validator;
import br.com.caelum.vraptor.view.FlashScope;

/**
 * An observer which instantiates parameters and provide them to the request.
 *
 * @author Guilherme Silveira
 * @author Rodrigo Turini
 * @author Victor Harada
 */
public class ParametersInstantiator {
	
	private static final Logger logger = getLogger(ParametersInstantiator.class);

	private final ParametersProvider provider;
	private final ParameterNameProvider parameterNameProvider;
	private final MethodInfo methodInfo;
	private final Validator validator;
	private final MutableRequest request;
	private final FlashScope flash;

	private final List<Message> errors = new ArrayList<>();

	/**
	 * @deprecated CDI eyes only
	 */
	protected ParametersInstantiator() {
		this(null, null, null, null, null, null);
	}

	@Inject
	public ParametersInstantiator(ParametersProvider provider, ParameterNameProvider parameterNameProvider, 
			MethodInfo methodInfo, Validator validator, MutableRequest request, FlashScope flash) {
		this.provider = provider;
		this.parameterNameProvider = parameterNameProvider;
		this.methodInfo = methodInfo;
		this.validator = validator;
		this.request = request;
		this.flash = flash;
	}

	public boolean containsParameters(ControllerMethod method) {
		return method.getMethod().getParameterTypes().length > 0;
	}

	public void instantiate(@Observes StackStarting event) {
		ControllerMethod controllerMethod = event.getControllerMethod();
		if (containsParameters(controllerMethod)) {
			fixIndexedParameters(request);
			addHeaderParametersToAttribute(controllerMethod);

			Object[] values = getParametersFor(controllerMethod);

			validator.addAll(errors);

			logger.debug("Conversion errors: {}", errors);
			logger.debug("Parameter values for {} are {}", controllerMethod, values);

			methodInfo.setParameters(values);
		}
	}

	private void addHeaderParametersToAttribute(ControllerMethod controllerMethod) {
		for (Parameter param : parameterNameProvider.parametersFor(controllerMethod.getMethod())) {
			HeaderParam headerParam = param.getAnnotation(HeaderParam.class);
			if (headerParam != null) {
				String value = request.getHeader(headerParam.value());
				request.setParameter(param.getName(), value);
			}
		}
	}

	private void fixIndexedParameters(MutableRequest request) {
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			disallowUsingClassAttribute(name);

			if (name.contains("[]")) {
				String[] values = request.getParameterValues(name);
				for (int i = 0; i < values.length; i++) {
					request.setParameter(name.replace("[]", "[" + i + "]"), values[i]);
				}
			}
		}
	}

	private void disallowUsingClassAttribute(String name) {
		checkArgument(!name.contains(".class."), "Bug Exploit Attempt with parameter: %s", name);
	}

	private Object[] getParametersFor(ControllerMethod method) {
		Object[] args = flash.consumeParameters(method);
		if (args == null) {
			return provider.getParametersFor(method, errors);
		}
		return args;
	}
}
