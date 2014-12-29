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
package br.com.caelum.vraptor.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.ExceptionMapper;
import br.com.caelum.vraptor.core.ExceptionRecorder;
import br.com.caelum.vraptor.core.InterceptorStack;

public class ExecuteMethodExceptionHandlerInterceptorTest {

	private Object instance;
	@Mock private InterceptorStack stack;
	@Mock private ControllerMethod method;
	@Mock private ExceptionMapper mapper;
	@Mock private Result result;
	@Mock private ExceptionRecorder<Result> mockRecorder;
	private ExceptionHandlerInterceptor interceptor;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		interceptor = new ExceptionHandlerInterceptor(mapper, result);
	}
	
	@Test
	public void shouldAlwaysAccept() {
		assertTrue(interceptor.accepts(null));
	}

	/**
	 * Test when the exception is found.
	 */
	@Test
	public void withRootException() {
		final Exception e = new IllegalStateException();
		when(mapper.findByException(e)).thenReturn(mockRecorder);
		doThrow(new InterceptionException(e)).when(stack).next(method, instance);

		interceptor.intercept(stack, method, instance);
		verify(mockRecorder).replay(result);
	}

	/**
	 * Test when the exception is not found, so vraptor needs only rethrows the
	 * exception.
	 */
	@Test
	public void whenNotFoundException() {
		final Exception e = new IllegalArgumentException();
		when(mapper.findByException(e)).thenReturn(null);
		doThrow(new InterceptionException(e)).when(stack).next(method, instance);

		try {
			interceptor.intercept(stack, method, instance);
			fail("Should throw InterceptionException");
		} catch (InterceptionException e2) {
			assertEquals(e2.getCause(), e);
		}
	}
}
