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

package br.com.caelum.vraptor4.interceptor;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.ControllerNotFoundHandler;
import br.com.caelum.vraptor4.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.core.MethodInfo;
import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.http.UrlToControllerTranslator;
import br.com.caelum.vraptor4.http.route.ControllerNotFoundException;
import br.com.caelum.vraptor4.http.route.MethodNotAllowedException;

/**
 * Looks up the controller for a specific request and delegates for the 404
 * component if unable to find it.
 *
 * @author Guilherme Silveira
 * @author Cecilia Fernandes
 */
@Intercepts(after={})
public class ControllerLookupInterceptor implements Interceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerLookupInterceptor.class);
	private UrlToControllerTranslator translator;
	private MethodInfo methodInfo;
	private RequestInfo requestInfo;
	private ControllerNotFoundHandler controllerNotFoundHandler;
	private MethodNotAllowedHandler methodNotAllowedHandler;
	private ControllerMethod method;

	//CDI eyes only
	@Deprecated
	public ControllerLookupInterceptor() {
	}

	@Inject
	public ControllerLookupInterceptor(UrlToControllerTranslator translator, MethodInfo methodInfo,
			ControllerNotFoundHandler controllerNotFoundHandler, MethodNotAllowedHandler methodNotAllowedHandler,
			RequestInfo requestInfo) {
		this.translator = translator;
		this.methodInfo = methodInfo;
		this.methodNotAllowedHandler = methodNotAllowedHandler;
		this.controllerNotFoundHandler = controllerNotFoundHandler;
		this.requestInfo = requestInfo;
	}

	@Override
	public void intercept(InterceptorStack stack, ControllerMethod ignorableMethod, Object controllerInstance)
			throws InterceptionException {

		try {
			method = translator.translate(requestInfo);

			methodInfo.setControllerMethod(method);
			stack.next(method, controllerInstance);
		} catch (ControllerNotFoundException e) {
			controllerNotFoundHandler.couldntFind(requestInfo);
		} catch (MethodNotAllowedException e) {
			LOGGER.debug(e.getMessage(), e);
			methodNotAllowedHandler.deny(requestInfo, e.getAllowedMethods());
		}
	}

	@Override
	public boolean accepts(ControllerMethod method) {
		return true;
	}

	@Produces
	@RequestScoped
	public ControllerMethod createControllerMethod() {
		return this.method;
	}
}
