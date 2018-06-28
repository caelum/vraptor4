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

package br.com.caelum.vraptor.core;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Iterator;
import java.util.LinkedList;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.events.InterceptorsExecuted;
import br.com.caelum.vraptor.events.InterceptorsReady;

/**
 * Default implementation of an {@link InterceptorStack}
 *
 * @author guilherme silveira, mariofts
 *
 */
@RequestScoped
public class DefaultInterceptorStack implements InterceptorStack {

	private static final Logger logger = getLogger(DefaultInterceptorStack.class);
	private final InterceptorStackHandlersCache cache;
	private final LinkedList<Iterator<InterceptorHandler>> internalStack = new LinkedList<>();
	private final Instance<ControllerMethod> controllerMethod;
	private final Instance<ControllerInstance> controllerInstance;
	private final Event<InterceptorsReady> interceptorsReadyEvent;
	private final Event<InterceptorsExecuted> interceptorsExecutedEvent;

	/**
	 * @deprecated CDI eyes only
	 */
	protected DefaultInterceptorStack() {
		this(null, null, null, null, null);
	}

	@Inject
	public DefaultInterceptorStack(InterceptorStackHandlersCache cache, Instance<ControllerMethod>
			controllerMethod, Instance<ControllerInstance> controllerInstance, Event<InterceptorsExecuted> event,
			Event<InterceptorsReady> stackStartingEvent) {
		this.cache = cache;
		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
		this.interceptorsExecutedEvent = event;
		this.interceptorsReadyEvent = stackStartingEvent;
	}

	@Override
	public void next(ControllerMethod method, Object controllerInstance) throws InterceptionException {
		Iterator<InterceptorHandler> iterator = internalStack.peek();

		if (!iterator.hasNext()) {
			interceptorsExecutedEvent.fire(new InterceptorsExecuted(controllerMethod.get(), controllerInstance));
			logger.debug("All registered interceptors have been called. End of VRaptor Request Execution.");
			return;
		}
		InterceptorHandler handler = iterator.next();
		handler.execute(this, method, controllerInstance);

	}

	@Override
	public void start() {
		ControllerMethod method = controllerMethod.get();
		interceptorsReadyEvent.fire(new InterceptorsReady(method));
		LinkedList<InterceptorHandler> handlers = cache.getInterceptorHandlers();
		internalStack.addFirst(handlers.iterator());
		this.next(method, controllerInstance.get().getController());
		internalStack.poll();
	}
}
