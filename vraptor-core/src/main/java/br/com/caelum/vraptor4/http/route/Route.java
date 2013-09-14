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

import java.lang.reflect.Method;
import java.util.EnumSet;

import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.HttpMethod;
import br.com.caelum.vraptor4.http.MutableRequest;

/**
 * An specific route rule.
 *
 * @author Guilherme Silveira
 */
public interface Route {

	/**
	 * Returns the controller method for this specifig rule. Also applies the
	 * required parameters to this vraptor request.
	 * You must call {@link Route#canHandle(String)} method, and see if request
	 * HTTP method is in {@link Route#allowedMethods()} before calling this method.
	 */
	ControllerMethod controllerMethod(MutableRequest request, String uri);

	/**
	 * Returns if this route can handle this URI
	 */
	boolean canHandle(String uri);

	/**
	 * @return all allowed HTTP methods for this Route
	 */
	EnumSet<HttpMethod> allowedMethods();

	/**
	 * Returns the url which invokes this rule with values extracted from this
	 * parameter object. The object contains getters representing each method's
	 * parameter.
	 */
	String urlFor(Class<?> type, Method m, Object... params);

	/**
	 * Returns true if this route is able to redirect to this method.
	 */
	boolean canHandle(Class<?> type, Method method);

	/**
	 * Routes will be ordered according to priority.
	 * Routes with lower values of priority will be tested first.
	 * @return route priority
	 */
	int getPriority();

	/**
	 * Returns the original uri for this route
	 */
	String getOriginalUri();

	ControllerMethod getControllerMethod();
}
