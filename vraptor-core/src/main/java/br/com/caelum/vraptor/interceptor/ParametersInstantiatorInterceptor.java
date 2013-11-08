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

package br.com.caelum.vraptor.interceptor;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.HeaderParam;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.ReadyToExecuteMethod;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.Validator;
import br.com.caelum.vraptor.view.FlashScope;

/**
 * An interceptor which instantiates parameters and provide them to the stack.
 *
 * @author Guilherme Silveira
 */
@Intercepts(after=ControllerLookupInterceptor.class)
public class ParametersInstantiatorInterceptor implements Interceptor {
	private static final Logger logger = LoggerFactory.getLogger(ParametersInstantiatorInterceptor.class);

	private final ParametersProvider provider;
	private final ParameterNameProvider parameterNameProvider;
	private final MethodInfo parameters;
	private final Validator validator;
	private final MutableRequest request;
	private final FlashScope flash;
	private Event<ReadyToExecuteMethod> readyToExecuteMethod;

	private final List<Message> errors = new ArrayList<>();

	/**
	 * @deprecated CDI eyes only
	 */
	protected ParametersInstantiatorInterceptor() {
		this(null, null, null, null, null, null, null);
	}

	@Inject
	public ParametersInstantiatorInterceptor(ParametersProvider provider, ParameterNameProvider parameterNameProvider, MethodInfo parameters,
			Validator validator, MutableRequest request, FlashScope flash, Event<ReadyToExecuteMethod> readyToExecuteMethod) {
		this.provider = provider;
		this.parameterNameProvider = parameterNameProvider;
		this.parameters = parameters;
		this.validator = validator;
		this.request = request;
		this.flash = flash;
		this.readyToExecuteMethod = readyToExecuteMethod;
	}

	@Override
	public boolean accepts(ControllerMethod method) {
		return method.getMethod().getParameterTypes().length > 0;
	}

	@Override
	public void intercept(InterceptorStack stack, ControllerMethod method, Object controllerInstance) throws InterceptionException {
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			fixParameter(names.nextElement());
		}

		addHeaderParametersToAttribute(method);

		Object[] values = getParametersFor(method);

		validator.addAll(errors);

		logger.debug("Conversion errors: {}", errors);
		logger.debug("Parameter values for {} are {}", method, values);

		parameters.setParameters(values);

		readyToExecuteMethod.fire(new ReadyToExecuteMethod(method));

		stack.next(method, controllerInstance);
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

	private void fixParameter(String name) {
		checkArgument(!name.contains(".class."), "Bug Exploit Attempt with parameter: %s", name);

		if (name.contains("[]")) {
			String[] values = request.getParameterValues(name);
			for (int i = 0; i < values.length; i++) {
				request.setParameter(name.replace("[]", "[" + i + "]"), values[i]);
			}
		}
	}

	private Object[] getParametersFor(ControllerMethod method) {
		Object[] args = flash.consumeParameters(method);
		if (args == null) {
			return provider.getParametersFor(method, errors);
		}
		return args;
	}
}
