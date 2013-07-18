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
package br.com.caelum.vraptor4.core;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.Lazy;
import br.com.caelum.vraptor4.interceptor.AspectStyleInterceptorHandler;
import br.com.caelum.vraptor4.interceptor.Interceptor;
import br.com.caelum.vraptor4.interceptor.StepInvoker;
import br.com.caelum.vraptor4.ioc.ApplicationScoped;
import br.com.caelum.vraptor4.ioc.Container;

import com.google.common.collect.MapMaker;

/**
 *
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 3.2.0
 *
 */
@ApplicationScoped
@Default
public class DefaultInterceptorHandlerFactory implements InterceptorHandlerFactory {

	private Container container;

	private ConcurrentMap<Class<?>, InterceptorHandler> cachedHandlers =
		new MapMaker().makeMap();

	//CDI eyes only
	@Deprecated
	public DefaultInterceptorHandlerFactory() {
	}
	
	@Inject
	public DefaultInterceptorHandlerFactory(Container container) {
		this.container = container;
	}
	
	@Override
	public InterceptorHandler handlerFor(Class<?> type) {
		if(cachedHandlers.containsKey(type)){
			return cachedHandlers.get(type);
		}
		if (type.isAnnotationPresent(Lazy.class)) {
			InterceptorHandler handler = cachedHandlers.get(type);
			if (handler == null) {
				LazyInterceptorHandler value = new LazyInterceptorHandler(container, type);
				cachedHandlers.putIfAbsent(type, value);
				return value;
			} else {
				return handler;
			}
		}
		else{
			if(type.isAnnotationPresent(Intercepts.class) && !Interceptor.class.isAssignableFrom(type)){
				AspectStyleInterceptorHandler handler = new AspectStyleInterceptorHandler(type, new StepInvoker(), container);
				cachedHandlers.putIfAbsent(type,handler);
				return handler;
			}
		}
		return new ToInstantiateInterceptorHandler(container, type);
	}

}
