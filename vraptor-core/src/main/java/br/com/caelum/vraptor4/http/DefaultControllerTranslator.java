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

package br.com.caelum.vraptor4.http;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor4.http.route.Router;
import br.com.caelum.vraptor4.ioc.ApplicationScoped;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4.restfulie.controller.HttpMethod;

/**
 * Basic url to controller method translator.
 *
 * @author Guilherme Silveira
 * @author Leonardo Bessa
 */
@ApplicationScoped
public class DefaultControllerTranslator implements UrlToControllerTranslator {

	private final Logger logger = LoggerFactory.getLogger(DefaultControllerTranslator.class);

	private Router router;

	//CDI eyes only
	@Deprecated
	public DefaultControllerTranslator() {
	}

	@Inject
	public DefaultControllerTranslator(Router router) {
		this.router = router;
	}

	@Override
	public ControllerMethod translate(RequestInfo info) {
		MutableRequest request = info.getRequest();
		String controllerName = info.getRequestedUri();

		logger.debug("trying to access {}", controllerName);

		HttpMethod method;
		try {
			method = HttpMethod.of(request);
		} catch (IllegalArgumentException e) {
			throw new MethodNotAllowedException(router.allowedMethodsFor(controllerName), request.getMethod());
		}
		ControllerMethod controller = router.parse(controllerName, method, request);

		logger.debug("found resource {}", controller);
		return controller;
	}

}
