/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.core;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.interceptor.AspectStyleInterceptorHandler;
import br.com.caelum.vraptor.interceptor.CustomAcceptsExecutor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorAcceptsExecutor;
import br.com.caelum.vraptor.interceptor.InterceptorExecutor;
import br.com.caelum.vraptor.interceptor.StepInvoker;
import br.com.caelum.vraptor.ioc.Container;

import br.com.caelum.vraptor.observer.ExecuteMethodExceptionHandler;
import com.google.common.base.Supplier;

/**
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 3.2.0
 */
@ApplicationScoped
public class DefaultInterceptorHandlerFactory implements InterceptorHandlerFactory {

	private final Container container;
	private final CacheStore<Class<?>, InterceptorHandler> cachedHandlers;
	private final StepInvoker stepInvoker;
	private final InterceptorAcceptsExecutor acceptsExecutor;
	private final CustomAcceptsExecutor customAcceptsExecutor;
	private final InterceptorExecutor interceptorExecutor;
	private final ExecuteMethodExceptionHandler executeMethodExceptionHandler;

	/**
	 * @deprecated CDI eyes only
	 */
	protected DefaultInterceptorHandlerFactory() {
		this(null, null, null, null, null, null, null);
	}

	@Inject
	public DefaultInterceptorHandlerFactory(Container container, StepInvoker stepInvoker,
			CacheStore<Class<?>, InterceptorHandler> cachedHandlers, InterceptorAcceptsExecutor acceptsExecutor,
			CustomAcceptsExecutor customAcceptsExecutor, InterceptorExecutor interceptorExecutor,
			ExecuteMethodExceptionHandler executeMethodExceptionHandler) {

		this.container = container;
		this.stepInvoker = stepInvoker;
		this.cachedHandlers = cachedHandlers;
		this.acceptsExecutor = acceptsExecutor;
		this.customAcceptsExecutor = customAcceptsExecutor;
		this.interceptorExecutor = interceptorExecutor;
		this.executeMethodExceptionHandler = executeMethodExceptionHandler;
	}

	@Override
	public InterceptorHandler handlerFor(final Class<?> type) {
		return cachedHandlers.fetch(type, new Supplier<InterceptorHandler>() {
			@Override
			public InterceptorHandler get() {
				if(type.isAnnotationPresent(Intercepts.class) && !Interceptor.class.isAssignableFrom(type)){
					return new AspectStyleInterceptorHandler(type, stepInvoker, container, customAcceptsExecutor,
							acceptsExecutor, interceptorExecutor);
				}
				return new ToInstantiateInterceptorHandler(container, type, executeMethodExceptionHandler);
			}
		});
	}
}
