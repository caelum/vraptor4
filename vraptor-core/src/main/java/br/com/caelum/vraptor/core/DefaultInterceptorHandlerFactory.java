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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.AspectStyleInterceptorHandler;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorMethodParametersResolver;
import br.com.caelum.vraptor.interceptor.StepInvoker;
import br.com.caelum.vraptor.ioc.Container;

/**
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 3.2.0
 */
@ApplicationScoped
@Default
public class DefaultInterceptorHandlerFactory implements InterceptorHandlerFactory {

	private Container container;

	private final Map<Class<?>, InterceptorHandler> cachedHandlers = new HashMap<>();

	private StepInvoker stepInvoker;

	private InterceptorMethodParametersResolver parametersResolver;

	//CDI eyes only
	@Deprecated
	public DefaultInterceptorHandlerFactory() {
	}

	@Inject
	public DefaultInterceptorHandlerFactory(Container container, StepInvoker
			stepInvoker, InterceptorMethodParametersResolver parametersResolver) {

		this.container = container;
		this.stepInvoker = stepInvoker;
		this.parametersResolver = parametersResolver;
	}

	@Override
	public InterceptorHandler handlerFor(Class<?> type) {

		InterceptorHandler interceptorHandler = cachedHandlers.get(type);
		if(interceptorHandler != null){
			return interceptorHandler;
		}

		if(type.isAnnotationPresent(Intercepts.class) && !Interceptor.class.isAssignableFrom(type)){
			AspectStyleInterceptorHandler handler = new AspectStyleInterceptorHandler(type, stepInvoker, container, parametersResolver);
			cachedHandlers.put(type,handler);

			return handler;
		}
		return new ToInstantiateInterceptorHandler(container, type);
	}
}
