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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import br.com.caelum.vraptor.observer.ExecuteMethodExceptionHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.interceptor.AspectStyleInterceptorHandler;
import br.com.caelum.vraptor.interceptor.CustomAcceptsExecutor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorAcceptsExecutor;
import br.com.caelum.vraptor.interceptor.InterceptorExecutor;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor.interceptor.StepInvoker;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultInterceptorHandlerFactoryTest {

	private Container container;

	private DefaultInterceptorHandlerFactory factory;

	private StepInvoker stepInvoker = new StepInvoker(new DefaultReflectionProvider());

	private @Mock SimpleInterceptorStack simpleStack;
	private @Mock InterceptorAcceptsExecutor acceptsExecutor;
	private @Mock CustomAcceptsExecutor customAcceptsExecutor;

	private InterceptorExecutor interceptorExecutor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		CacheStore<Class<?>, InterceptorHandler> cachedHandlers = new DefaultCacheStore<>();
		ExecuteMethodExceptionHandler executeMethodExceptionHandler = new ExecuteMethodExceptionHandler();
		factory = new DefaultInterceptorHandlerFactory(container, stepInvoker,
				cachedHandlers, acceptsExecutor, customAcceptsExecutor, interceptorExecutor, executeMethodExceptionHandler);
	}

	static interface RegularInterceptor extends Interceptor {}

	@Test
	public void handlerForRegularInterceptorsShouldBeDynamic() throws Exception {
		assertThat(factory.handlerFor(RegularInterceptor.class), is(instanceOf(ToInstantiateInterceptorHandler.class)));
	}

	@Test
	public void handlerForAspectStyleInterceptorsShouldBeDynamic() throws Exception {
		assertThat(factory.handlerFor(AspectStyleInterceptor.class), is(instanceOf(AspectStyleInterceptorHandler.class)));
	}

	@Test
	public void aspectStyleHandlersShouldBeCached() throws Exception {
		InterceptorHandler handler = factory.handlerFor(AspectStyleInterceptor.class);
		assertThat(factory.handlerFor(AspectStyleInterceptor.class), is(sameInstance(handler)));
	}
}
