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

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.ControllerNotFoundHandler;
import br.com.caelum.vraptor4.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor4.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor4.http.route.ResourceNotFoundException;

/**
 * Looks up the resource for a specific request and delegates for the 404
 * component if unable to find it.
 *
 * @author Guilherme Silveira
 * @author Cecilia Fernandes
 */
@Intercepts(after={})
public class ResourceLookupInterceptor implements Interceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLookupInterceptor.class);
	private UrlToResourceTranslator translator;
	private MethodInfo methodInfo;
	private RequestInfo requestInfo;
	private ControllerNotFoundHandler resourceNotFoundHandler;
	private MethodNotAllowedHandler methodNotAllowedHandler;
	private ControllerMethod method;

	//CDI eyes only
	@Deprecated
	public ResourceLookupInterceptor() {
	}
	
	@Inject
	public ResourceLookupInterceptor(UrlToResourceTranslator translator, MethodInfo methodInfo,
			ControllerNotFoundHandler resourceNotFoundHandler, MethodNotAllowedHandler methodNotAllowedHandler,
			RequestInfo requestInfo) {
		this.translator = translator;
		this.methodInfo = methodInfo;
		this.methodNotAllowedHandler = methodNotAllowedHandler;
		this.resourceNotFoundHandler = resourceNotFoundHandler;
		this.requestInfo = requestInfo;
	}
	
	@Override
	public void intercept(InterceptorStack stack, ControllerMethod ignorableMethod, Object resourceInstance)
			throws InterceptionException {

		try {
			method = translator.translate(requestInfo);

			methodInfo.setResourceMethod(method);
			stack.next(method, resourceInstance);
		} catch (ResourceNotFoundException e) {
			resourceNotFoundHandler.couldntFind(requestInfo);
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
	public ControllerMethod createControllerMethod() {
		return this.method;
	}
}
