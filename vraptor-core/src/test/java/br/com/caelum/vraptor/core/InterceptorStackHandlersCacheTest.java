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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.when;

public class InterceptorStackHandlersCacheTest {
	
	private @Mock InterceptorRegistry registry;
	private @Mock InterceptorHandlerFactory handlerFactory;
	private InterceptorStackHandlersCache cache;
	private List<Class<?>> interceptors;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		interceptors = new ArrayList<>();
		interceptors.add(FirstInterceptor.class);
		interceptors.add(SecondInterceptor.class);
		
		when(registry.all()).thenReturn(interceptors);
		
		when(handlerFactory.handlerFor(Mockito.any(Class.class))).thenAnswer(new Answer<InterceptorHandler>() {
			@Override
			public InterceptorHandler answer(InvocationOnMock invocation)
					throws Throwable {
				Object[] arguments = invocation.getArguments();
				return new MockInterceptorHandler((Class<?>) arguments[0]);
			}
		});
		
		cache = new InterceptorStackHandlersCache(registry, handlerFactory);
		cache.init();
	}

	@Test
	public void shouldReturnHandlersListInTheSameOrderThatRegistry() {
		
		LinkedList<InterceptorHandler> handlers = cache.getInterceptorHandlers();
		
		assertEquals(FirstInterceptor.class, extractInterceptor(handlers.get(0)));
		assertEquals(SecondInterceptor.class, extractInterceptor(handlers.get(1)));
	}
	
	@Test
	public void cacheShouldBeImmutable() {
		cache.getInterceptorHandlers().remove(0);
		assertEquals(2, cache.getInterceptorHandlers().size());
	}
	
	private Class<?> extractInterceptor(InterceptorHandler handler){
		return ((MockInterceptorHandler)handler).interceptor;
	}
	
	static interface FirstInterceptor extends Interceptor {}
	static interface SecondInterceptor extends Interceptor {}
	
	private class MockInterceptorHandler implements InterceptorHandler{

		private Class<?> interceptor;
		
		public MockInterceptorHandler(Class<?> interceptor) {
			this.interceptor = interceptor;
		}
		
		@Override
		public void execute(InterceptorStack stack, ControllerMethod method,
				Object controllerInstance) throws InterceptionException {
			//do nothing
		}
	}

}
