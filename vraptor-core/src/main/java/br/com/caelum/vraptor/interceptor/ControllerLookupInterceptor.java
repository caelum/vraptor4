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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.ControllerNotFoundHandler;
import br.com.caelum.vraptor.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.events.ControllerMethodDiscovered;
import br.com.caelum.vraptor.http.UrlToControllerTranslator;
import br.com.caelum.vraptor.http.route.ControllerNotFoundException;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;

/**
 * Looks up the controller for a specific request and delegates for the 404
 * component if unable to find it.
 *
 * @author Guilherme Silveira
 * @author Cecilia Fernandes
 */
@Intercepts(after={})
@Dependent
public class ControllerLookupInterceptor implements Interceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerLookupInterceptor.class);

	private final UrlToControllerTranslator translator;
	private final MethodInfo methodInfo;
	private final RequestInfo requestInfo;
	private final ControllerNotFoundHandler controllerNotFoundHandler;
	private final MethodNotAllowedHandler methodNotAllowedHandler;
	private final Event<ControllerMethodDiscovered> event;

	private ControllerMethod method;

	/**
	 * @deprecated CDI eyes only
	 */
	protected ControllerLookupInterceptor() {
		this(null, null, null, null, null, null);
	}

	@Inject
	public ControllerLookupInterceptor(UrlToControllerTranslator translator, MethodInfo methodInfo,
			ControllerNotFoundHandler controllerNotFoundHandler, MethodNotAllowedHandler methodNotAllowedHandler,
			RequestInfo requestInfo, Event<ControllerMethodDiscovered> event) {
		this.translator = translator;
		this.methodInfo = methodInfo;
		this.methodNotAllowedHandler = methodNotAllowedHandler;
		this.controllerNotFoundHandler = controllerNotFoundHandler;
		this.requestInfo = requestInfo;
		this.event = event;
	}

	@Override
	public void intercept(InterceptorStack stack, ControllerMethod ignorableMethod, Object controllerInstance)
			throws InterceptionException {

		try {
			method = translator.translate(requestInfo);
			event.fire(new ControllerMethodDiscovered(method));

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
}
