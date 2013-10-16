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
/**
 *
 */
package br.com.caelum.vraptor.ioc;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.core.ControllerQualifier;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.http.route.RoutesParser;

@ApplicationScoped
public class ControllerHandler{
	private final Logger logger = LoggerFactory.getLogger(ControllerHandler.class);
	private Router router;
	private RoutesParser parser;
	private ServletContext context;

	/** @Deprecated CDI eyes only */
	public ControllerHandler() {
	}

	@Inject
	public ControllerHandler(Router router, RoutesParser parser,
			ServletContext context) {
		this.router = router;
		this.parser = parser;
		this.context = context;
	}

	public void handle(@Observes @ControllerQualifier BeanClass annotatedType) {
		logger.debug("Found controller: {}", annotatedType);
		List<Route> routes = parser.rulesFor(annotatedType);
		for (Route route : routes) {
			router.add(route);
		}
		context.setAttribute(annotatedType.getType().getSimpleName(), annotatedType);
	}

}