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

package br.com.caelum.vraptor.http;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor.http.route.Router;

/**
 * Basic url to controller method translator.
 *
 * @author Guilherme Silveira
 * @author Leonardo Bessa
 */
@ApplicationScoped
public class DefaultControllerTranslator implements UrlToControllerTranslator {

	private final Logger logger = LoggerFactory.getLogger(DefaultControllerTranslator.class);

	private final Router router;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultControllerTranslator() {
		this(null);
	}

	@Inject
	public DefaultControllerTranslator(Router router) {
		this.router = router;
	}

	@Override
	public ControllerMethod translate(MutableRequest request) {
		String controllerName = request.getRequestedUri();

		logger.debug("trying to access {}", controllerName);

		HttpMethod method = getHttpMethod(request, controllerName);
		ControllerMethod controller = router.parse(controllerName, method, request);

		logger.debug("found controller {}", controller);
		return controller;
	}

	private HttpMethod getHttpMethod(MutableRequest request, String controllerName) {
		try {
			return HttpMethod.of(request);
		} catch (IllegalArgumentException e) {
			throw new MethodNotAllowedException(router.allowedMethodsFor(controllerName), request.getMethod());
		}
	}
}
