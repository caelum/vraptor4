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

package br.com.caelum.vraptor.observer;

import static org.slf4j.LoggerFactory.getLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.ControllerNotFoundHandler;
import br.com.caelum.vraptor.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.events.ControllerMethodDiscovered;
import br.com.caelum.vraptor.events.NewRequest;
import br.com.caelum.vraptor.http.UrlToControllerTranslator;
import br.com.caelum.vraptor.http.route.ControllerNotFoundException;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;

/**
 * Looks up the controller for a specific request and delegates for the 404
 * component if unable to find it.
 *
 * @author Guilherme Silveira
 * @author Cecilia Fernandes
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class ControllerLookupObserver {

	private static final Logger LOGGER = getLogger(ControllerLookupObserver.class);

	private final UrlToControllerTranslator translator;
	private final ControllerNotFoundHandler controllerNotFoundHandler;
	private final MethodNotAllowedHandler methodNotAllowedHandler;
	private final Event<ControllerMethodDiscovered> controllerMethodEvent;

	/**
	 * @deprecated CDI eyes only
	 */
	protected ControllerLookupObserver() {
		this(null, null, null, null);
	}

	@Inject
	public ControllerLookupObserver(UrlToControllerTranslator translator,
			ControllerNotFoundHandler controllerNotFoundHandler, MethodNotAllowedHandler methodNotAllowedHandler,
			Event<ControllerMethodDiscovered> event) {
		this.translator = translator;
		this.methodNotAllowedHandler = methodNotAllowedHandler;
		this.controllerNotFoundHandler = controllerNotFoundHandler;
		this.controllerMethodEvent = event;
	}

	public void lookup(@Observes NewRequest event, MethodInfo methodInfo, RequestInfo requestInfo) {
		try {
			ControllerMethod method = translator.translate(requestInfo);
			controllerMethodEvent.fire(new ControllerMethodDiscovered(method));
			methodInfo.setControllerMethod(method);
		} catch (ControllerNotFoundException e) {
			controllerNotFoundHandler.couldntFind(requestInfo);
		} catch (MethodNotAllowedException e) {
			LOGGER.debug(e.getMessage(), e);
			methodNotAllowedHandler.deny(requestInfo, e.getAllowedMethods());
		}
	}
}