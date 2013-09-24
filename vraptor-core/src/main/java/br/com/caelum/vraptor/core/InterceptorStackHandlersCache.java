package br.com.caelum.vraptor.core;

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

import java.util.LinkedList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.com.caelum.vraptor.interceptor.InterceptorRegistry;

@ApplicationScoped
public class InterceptorStackHandlersCache {

	private final LinkedList<InterceptorHandler> interceptorHandlers = new LinkedList<>();
	private InterceptorRegistry registry;
	private InterceptorHandlerFactory handlerFactory;

	@Inject private Logger logger;

	@Deprecated //CDI eyes only
	public InterceptorStackHandlersCache() {}

	@Inject
	public InterceptorStackHandlersCache(InterceptorRegistry registry,
			InterceptorHandlerFactory handlerFactory){

		this.registry = registry;
		this.handlerFactory = handlerFactory;
	}

	public void init() {
		for (Class<?> interceptor : registry.all()) {
			logger.debug("Caching {} ", interceptor.getName());
			InterceptorHandler handlerFor = handlerFactory.handlerFor(interceptor);
			this.interceptorHandlers.addLast(handlerFor);
		}
	}

	public LinkedList<InterceptorHandler> getInterceptorHandlers() {
		return new LinkedList<>(interceptorHandlers);
	}

}