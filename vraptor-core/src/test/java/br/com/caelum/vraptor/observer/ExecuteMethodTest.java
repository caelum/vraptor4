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

import static br.com.caelum.vraptor.view.Results.nothing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Collections;

import javax.enterprise.event.Event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.EndOfInterceptorStack;
import br.com.caelum.vraptor.events.MethodExecuted;
import br.com.caelum.vraptor.events.ReadyToExecuteMethod;
import br.com.caelum.vraptor.factory.Factories;
import br.com.caelum.vraptor.interceptor.DogAlike;
import br.com.caelum.vraptor.reflection.MethodExecutor;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.validator.Validator;

public class ExecuteMethodTest {

	@Mock private MethodInfo methodInfo;
	@Mock private Validator validator;
	@Mock private Event<MethodExecuted> methodEvecutedEvent;
	@Mock private Event<ReadyToExecuteMethod> readyToExecuteMethodEvent;
	private MethodExecutor methodExecutor = Factories.createMethodExecutor();
	private ExecuteMethod observer;

	@Before
	public void setup() throws NoSuchMethodException {
		MockitoAnnotations.initMocks(this);
		observer = new ExecuteMethod(methodInfo, validator, methodExecutor, methodEvecutedEvent, readyToExecuteMethodEvent);
	}

	@Test
	public void shouldInvokeTheMethodAndNotProceedWithInterceptorStack() throws Exception {
		ControllerMethod method = new DefaultControllerMethod(null, DogAlike.class.getMethod("bark"));
		DogAlike auau = mock(DogAlike.class);
		when(methodInfo.getParametersValues()).thenReturn(new Object[0]);
		observer.execute(new EndOfInterceptorStack(method, auau));
		verify(auau).bark();
	}

	@Test
	public void shouldThrowMethodExceptionIfThereIsAnInvocationException() throws Exception {
		ControllerMethod method = new DefaultControllerMethod(null, DogAlike.class.getMethod("bark"));
		final DogAlike auau = mock(DogAlike.class);
		final RuntimeException exception = new RuntimeException();
		doThrow(exception).when(auau).bark();
		when(methodInfo.getParametersValues()).thenReturn(new Object[0]);
		try {
			observer.execute(new EndOfInterceptorStack(method, auau));
			Assert.fail();
		} catch (InterceptionException e) {
			assertThat((RuntimeException) e.getCause(), is(equalTo(exception)));
		}
	}

	@Test
	public void shouldUseTheProvidedArguments() throws Exception {
		ControllerMethod method = new DefaultControllerMethod(null, DogAlike.class.getMethod("bark", int.class));
		DogAlike auau = mock(DogAlike.class);
		when(methodInfo.getParametersValues()).thenReturn(new Object[] { 3 });
		observer.execute(new EndOfInterceptorStack(method, auau));
		verify(auau).bark(3);
	}

	public static class XController {
		public Object method(Object o) {
			return o;
		}
		public void method() {
		}
	}

	@Test
	public void shouldSetResultReturnedValueFromInvokedMethod() throws Exception {
		ControllerMethod method = new DefaultControllerMethod(null, XController.class.getMethod("method", Object.class));
		final XController controller = new XController();
		when(methodInfo.getParametersValues()).thenReturn(new Object[] { "string" });
		observer.execute(new EndOfInterceptorStack(method, controller));
	}

	@Test
	public void shouldSetNullWhenNullReturnedFromInvokedMethod() throws Exception {
		ControllerMethod method = new DefaultControllerMethod(null, XController.class.getMethod("method", Object.class));
		final XController controller = new XController();
		when(methodInfo.getParametersValues()).thenReturn(new Object[] { null });
		observer.execute(new EndOfInterceptorStack(method, controller));
		verify(methodInfo).setResult(null);
	}

	@Test
	public void shouldBeOkIfThereIsValidationErrorsAndYouSpecifiedWhereToGo() throws Exception {
		Method specifiedWhereToGo = AnyController.class.getMethod("specifiedWhereToGo");
		ControllerMethod method = DefaultControllerMethod.instanceFor(AnyController.class, specifiedWhereToGo);
		AnyController controller = new AnyController(validator);
		when(methodInfo.getParametersValues()).thenReturn(new Object[0]);
		doThrow(new ValidationException(Collections.<Message> emptyList())).when(validator).onErrorUse(nothing());
		when(validator.hasErrors()).thenReturn(true);
		observer.execute(new EndOfInterceptorStack(method, controller));
	}

	@Test(expected=InterceptionException.class)
	public void shouldThrowExceptionIfYouHaventSpecifiedWhereToGoOnValidationError() throws Exception {
		Method didntSpecifyWhereToGo = AnyController.class.getMethod("didntSpecifyWhereToGo");
		final ControllerMethod method = DefaultControllerMethod.instanceFor(AnyController.class, didntSpecifyWhereToGo);
		final AnyController controller = new AnyController(validator);
		when(methodInfo.getParametersValues()).thenReturn(new Object[0]);
		when(validator.hasErrors()).thenReturn(true);
		observer.execute(new EndOfInterceptorStack(method, controller));
	}

	public static class AnyController {
		private final Validator validator;

		public AnyController(Validator validator) {
			this.validator = validator;
		}
		public void didntSpecifyWhereToGo() {

		}
		public void specifiedWhereToGo() {
			this.validator.onErrorUse(nothing());
		}
	}
}