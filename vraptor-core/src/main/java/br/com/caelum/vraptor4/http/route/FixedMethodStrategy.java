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

package br.com.caelum.vraptor4.http.route;

import static com.google.common.base.Objects.equal;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

import br.com.caelum.vraptor4.http.MutableRequest;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4.restfulie.controller.HttpMethod;
import br.com.caelum.vraptor4.util.Stringnifier;

/**
 * A route strategy which invokes a fixed type's method.
 *
 * @author guilherme silveira
 */
public class FixedMethodStrategy implements Route {

	private final ControllerMethod controllerMethod;

	private final EnumSet<HttpMethod> methods;

	private final ParametersControl parameters;

	private final int priority;

	private final String originalUri;

	private final String[] parameterNames;


	public FixedMethodStrategy(String originalUri, ControllerMethod method, Set<HttpMethod> methods,
			ParametersControl control, int priority, String[] parameterNames) {
		this.originalUri = originalUri;
		this.parameterNames = parameterNames;
		this.methods = methods.isEmpty() ? EnumSet.allOf(HttpMethod.class) : EnumSet.copyOf(methods);
		this.parameters = control;
		this.controllerMethod = method;
		this.priority = priority;
	}

	public boolean canHandle(Class<?> type, Method method) {
		return type.equals(this.controllerMethod.getController().getType())
				&& method.equals(this.controllerMethod.getMethod());
	}

	public ControllerMethod controllerMethod(MutableRequest request, String uri) {
		parameters.fillIntoRequest(uri, request);
		return this.controllerMethod;
	}

	public EnumSet<HttpMethod> allowedMethods() {
		return methods;
	}

	public boolean canHandle(String uri) {
		return parameters.matches(uri);
	}

	public String urlFor(Class<?> type, Method m, Object... params) {
		return parameters.fillUri(parameterNames, params);
	}

	public int getPriority() {
		return this.priority;
	}

	public String getOriginalUri() {
		return this.originalUri;
	}

	@Override
	public String toString() {
		return String.format("[FixedMethodStrategy: %-65s %-70s %s]", originalUri, Stringnifier
				.simpleNameFor(controllerMethod.getMethod()), methods.size() == HttpMethod.values().length ? "ALL" : methods);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methods == null) ? 0 : methods.hashCode());
		result = prime * result + ((originalUri == null) ? 0 : originalUri.hashCode());
		result = prime * result + ((controllerMethod == null) ? 0 : controllerMethod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FixedMethodStrategy other = (FixedMethodStrategy) obj;
		return equal(methods, other.methods) && equal(originalUri, other.originalUri) && equal(controllerMethod,other.controllerMethod);
	}
}
