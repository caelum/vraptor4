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

package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.Parameter;

/**
 * A route strategy which invokes a fixed type's method.
 *
 * @author guilherme silveira
 */
@Vetoed
public class FixedMethodStrategy implements Route {

	private final ControllerMethod controllerMethod;
	private final EnumSet<HttpMethod> methods;
	private final ParametersControl parameters;
	private final int priority;
	private final String originalUri;
	private final Parameter[] parameterNames;

	public FixedMethodStrategy(String originalUri, ControllerMethod method, Set<HttpMethod> methods,
			ParametersControl control, int priority, Parameter[] parameterNames) {
		this.originalUri = originalUri;
		this.parameterNames = parameterNames;
		this.methods = methods.isEmpty() ? EnumSet.allOf(HttpMethod.class) : EnumSet.copyOf(methods);
		this.parameters = control;
		this.controllerMethod = method;
		this.priority = priority;
	}

	@Override
	public boolean canHandle(Class<?> type, Method method) {
		Method getMethod = this.controllerMethod.getMethod();
		return type.equals(this.controllerMethod.getController().getType())
			&& method.getName().equals(getMethod.getName())
			&& Arrays.equals(method.getParameterTypes(), getMethod.getParameterTypes());
	}

	@Override
	public ControllerMethod controllerMethod(MutableRequest request, String uri) {
		parameters.fillIntoRequest(uri, request);
		return this.controllerMethod;
	}

	@Override
	public EnumSet<HttpMethod> allowedMethods() {
		return methods;
	}

	@Override
	public boolean canHandle(String uri) {
		return parameters.matches(uri);
	}

	@Override
	public String urlFor(Class<?> type, Method m, Object... params) {
		return parameters.fillUri(parameterNames, params);
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public String getOriginalUri() {
		return this.originalUri;
	}

	@Override
	public ControllerMethod getControllerMethod() {
		return controllerMethod;
	}

	@Override
	public String toString() {
		return String.format("[FixedMethodStrategy: %-65s %-70s %s]", originalUri, 
			controllerMethod.getMethod().getName(), methods.size() == HttpMethod.values().length ? "ALL" : methods);
	}

	@Override
	public int hashCode() {
		return Objects.hash(methods, originalUri, controllerMethod);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		FixedMethodStrategy other = (FixedMethodStrategy) obj;
		return Objects.equals(methods, other.methods) 
				&& Objects.equals(originalUri, other.originalUri) 
				&& Objects.equals(controllerMethod,other.controllerMethod);
	}
}
