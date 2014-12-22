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

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.observer.ExecuteMethodExceptionHandler;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.*;

public class ToInstantiateInterceptorHandlerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private @Mock Container container;
	private @Mock Interceptor interceptor;
	private @Mock InterceptorStack stack;
	private @Mock ControllerMethod method;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	public static class MyWeirdInterceptor implements Interceptor {
		public MyWeirdInterceptor(Dependency d) {
		}

		@Override
		public void intercept(InterceptorStack stack, ControllerMethod method, Object resourceInstance)
				throws InterceptionException {
		}

		@Override
		public boolean accepts(ControllerMethod method) {
			return true;
		}
	}

	public static class Dependency {

	}

	@Test
	public void shouldComplainWhenUnableToInstantiateAnInterceptor() throws InterceptionException, IOException {
		exception.expect(InterceptionException.class);
		exception.expectMessage(containsString("Unable to instantiate interceptor for"));

		when(container.instanceFor(MyWeirdInterceptor.class)).thenReturn(null);

		ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container,
				MyWeirdInterceptor.class, new ExecuteMethodExceptionHandler());
		handler.execute(null, null, null);
	}

	@Test
	public void shouldInvokeInterceptorsMethodIfAbleToInstantiateIt() throws InterceptionException, IOException {
		final Object instance = new Object();
		
		when(container.instanceFor(Interceptor.class)).thenReturn(interceptor);
		when(interceptor.accepts(method)).thenReturn(true);

		ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container, Interceptor.class, new ExecuteMethodExceptionHandler());
		handler.execute(stack, method, instance);

		verify(interceptor).intercept(stack, method, instance);
	}
	@Test
	public void shouldNotInvokeInterceptorsMethodIfInterceptorDoesntAcceptsResource() throws InterceptionException, IOException {
		final Object instance = new Object();
		when(container.instanceFor(Interceptor.class)).thenReturn(interceptor);
		when(interceptor.accepts(method)).thenReturn(false);

			ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container, Interceptor.class, new ExecuteMethodExceptionHandler());
			handler.execute(stack, method, instance);
		
		verify(interceptor, never()).intercept(stack, method, instance);
		verify(stack).next(method, instance);
	}

	@Test
	public void shouldCatchValidationExceptionOfValidatedInterceptor() {
		MyValidatedInterceptor validatedInterceptor = new MyValidatedInterceptor();
		when(container.instanceFor(MyValidatedInterceptor.class)).thenReturn(validatedInterceptor);
		ExecuteMethodExceptionHandler exceptionHandler = Mockito.spy(new ExecuteMethodExceptionHandler());
		ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container, MyValidatedInterceptor.class, exceptionHandler);

		handler.execute(stack, method, new Object());
		verify(exceptionHandler).handle(Mockito.any(ValidationException.class));
	}

	private static class MyValidatedInterceptor implements Interceptor {
		@Override
		public void intercept(InterceptorStack stack, ControllerMethod method, Object controllerInstance) throws InterceptionException {
			throw new ValidationException(new ArrayList<Message>());
		}

		@Override
		public boolean accepts(ControllerMethod method) {
			return true;
		}
	}
}
