/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.iogi;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.validator.Message;

@RequestScoped
public class IogiParametersProvider implements ParametersProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(IogiParametersProvider.class);
	
	private final ParameterNameProvider nameProvider;
	private final HttpServletRequest servletRequest;
	private final InstantiatorWithErrors instantiator;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected IogiParametersProvider() {
		this(null, null, null);
	}
	
	@Inject
	public IogiParametersProvider(ParameterNameProvider provider, HttpServletRequest parameters, InstantiatorWithErrors instantiator) {
		this.nameProvider = provider;
		this.servletRequest = parameters;
		this.instantiator = instantiator;
		LOGGER.debug("IogiParametersProvider is up");
	}
	
	@Override
	public Object[] getParametersFor(ControllerMethod method, List<Message> errors) {
		Parameters parameters = parseParameters(servletRequest);
		List<Target<Object>> targets = createTargets(method);

		return instantiateParameters(parameters, targets, errors).toArray();
	}

	private List<Object> instantiateParameters(Parameters parameters, List<Target<Object>> targets, List<Message> errors) {
		LOGGER.debug("getParametersFor() called with parameters {} and targets {}.", parameters, targets);

		List<Object> arguments = new ArrayList<>(targets.size());
		for (Target<Object> target : targets) {
			Object newObject = instantiateOrAddError(parameters, errors, target);
			arguments.add(newObject);
		}
		return arguments;
	}

	private Object instantiateOrAddError(Parameters parameters, List<Message> errors, Target<Object> target) {
		return instantiator.instantiate(target, parameters, errors);
	}

	private List<Target<Object>> createTargets(ControllerMethod method) {
		Method javaMethod = method.getMethod();
		List<Target<Object>> targets = new ArrayList<>();

		for (Parameter p : nameProvider.parametersFor(javaMethod)) {
			Type type = p.getParameterizedType();
			if (type instanceof TypeVariable) {
				type = extractType(method, (TypeVariable<?>) type);
			}

			targets.add(new Target<>(type, p.getName()));
		}

		return targets;
	}

	private Type extractType(ControllerMethod method, TypeVariable<?> paramType) {
		ParameterizedType parameterizedType = (ParameterizedType) method.getController().getType().getGenericSuperclass();
		Class<?> rawType = (Class<?>) parameterizedType.getRawType();
		TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
		if (typeParameters.length > 0) {
			for (int i = 0; i < typeParameters.length; i++) {
				TypeVariable<?> typeVariable = typeParameters[i];
				if (typeVariable.getName().equals(paramType.getName())) {
					return (Class<?>) parameterizedType.getActualTypeArguments()[i];
				}
			}
		}
		return paramType;
	}

	private Parameters parseParameters(HttpServletRequest request) {
		Map<String, String[]> parameters = request.getParameterMap();
		List<br.com.caelum.iogi.parameters.Parameter> parameterList = new ArrayList<>(parameters.size() * 2);

		for (Entry<String, String[]> param: parameters.entrySet()) {
			for (String value : param.getValue()) {
				parameterList.add(new br.com.caelum.iogi.parameters.Parameter(param.getKey(), value));
			}
		}

		return new Parameters(parameterList);
	}
}
