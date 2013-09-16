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
import java.util.EnumSet;
import java.util.List;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Handles different rules in order to translate urls into Controller methods.
 *
 * @author Guilherme Silveira
 */
public interface Router {

	/**
	 * Add a new Route to this Router
	 * @param route The route
	 */
	void add(Route route);


	/**
	 * Find a {@link ControllerMethod} that can handle this request.
	 * @throws ControllerNotFoundException when there is no {@link ControllerMethod} that can handle
	 *         this request
	 * @throws MethodNotAllowedException when there is no {@link ControllerMethod} that can handle
	 *         given URI with given {@link HttpMethod}
	 * @throws IllegalStateException when more than one {@link ControllerMethod} can handle this
	 * 		   request.
	 */
	ControllerMethod parse(String uri, HttpMethod method, MutableRequest request)
		throws ControllerNotFoundException, MethodNotAllowedException, IllegalStateException;

	/**
	 * Retrieves a single url to access the desired method.
	 */
	<T> String urlFor(Class<T> type, Method method, Object... params);

	/**
	 * Returns a list with all routes
	 */
	List<Route> allRoutes();

	RouteBuilder builderFor(String uri);

	EnumSet<HttpMethod> allowedMethodsFor(String uri);
}
