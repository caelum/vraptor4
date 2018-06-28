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

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.InterceptorsExecuted;
import br.com.caelum.vraptor.events.MethodExecuted;
import br.com.caelum.vraptor.events.MethodReady;
import br.com.caelum.vraptor.interceptor.ApplicationLogicException;
import br.com.caelum.vraptor.interceptor.DogAlike;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.Messages;
import br.com.caelum.vraptor.validator.ValidationFailedException;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.validator.ValidationFailedException;
import br.com.caelum.vraptor.validator.Validator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.enterprise.event.Event;

import java.lang.reflect.Method;
import java.util.Collections;

import static br.com.caelum.vraptor.controller.DefaultControllerMethod.instanceFor;
import static br.com.caelum.vraptor.view.Results.nothing;
import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.*;

public class ExecuteMethodTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock private MethodInfo methodInfo;
	@Mock private Messages messages;
	@Mock private Validator validator;
	@Mock private Event<MethodExecuted> methodEvecutedEvent;
	@Mock private Event<MethodReady> readyToExecuteMethodEvent;
	@Rule public ExpectedException expected = ExpectedException.none();
	private ExecuteMethod observer;

	@Before
	public void setup() throws NoSuchMethodException {
		MockitoAnnotations.initMocks(this);
		observer = new ExecuteMethod(methodInfo, messages, methodEvecutedEvent, readyToExecuteMethodEvent,
				new ExecuteMethodExceptionHandler(), new DefaultReflectionProvider());
	}

	@Test
	public void shouldInvokeTheMethodAndNotProceedWithInterceptorStack() throws Exception {
		ControllerMethod method = new DefaultControllerMethod(null, DogAlike.class.getMethod("bark"));
		DogAlike auau = mock(DogAlike.class);
		when(methodInfo.getParametersValues()).thenReturn(new Object[0]);
		observer.execute(new InterceptorsExecuted(method, auau));
		verify(auau).bark();
		verify(messages).assertAbsenceOfErrors();
	}

	@Test
	public void shouldUseTheProvidedArguments() throws Exception {
		ControllerMethod method = new DefaultControllerMethod(null, DogAlike.class.getMethod("bark", int.class));
		DogAlike auau = mock(DogAlike.class);
		when(methodInfo.getParametersValues()).thenReturn(new Object[] { 3 });
		observer.execute(new InterceptorsExecuted(method, auau));
		verify(auau).bark(3);
		verify(messages).assertAbsenceOfErrors();
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
		observer.execute(new InterceptorsExecuted(method, controller));
		verify(messages).assertAbsenceOfErrors();
	}

	@Test
	public void shouldSetNullWhenNullReturnedFromInvokedMethod() throws Exception {
		ControllerMethod method = new DefaultControllerMethod(null, XController.class.getMethod("method", Object.class));
		final XController controller = new XController();
		when(methodInfo.getParametersValues()).thenReturn(new Object[] { null });
		observer.execute(new InterceptorsExecuted(method, controller));
		verify(methodInfo).setResult(null);
		verify(messages).assertAbsenceOfErrors();
	}

	@Test
	public void shouldBeOkIfThereIsValidationErrorsAndYouSpecifiedWhereToGo() throws Exception {
		Method specifiedWhereToGo = AnyController.class.getMethod("specifiedWhereToGo");
		ControllerMethod method = DefaultControllerMethod.instanceFor(AnyController.class, specifiedWhereToGo);
		AnyController controller = new AnyController(validator);
		when(methodInfo.getParametersValues()).thenReturn(new Object[0]);
		doThrow(new ValidationException(Collections.<Message> emptyList())).when(validator).onErrorUse(nothing());
		observer.execute(new InterceptorsExecuted(method, controller));
	}

	@Test
	public void shouldThrowExceptionIfYouHaventSpecifiedWhereToGoOnValidationError() throws Exception {
		exception.expect(ValidationFailedException.class);

		Method didntSpecifyWhereToGo = AnyController.class.getMethod("didntSpecifyWhereToGo");
		final ControllerMethod method = DefaultControllerMethod.instanceFor(AnyController.class, didntSpecifyWhereToGo);
		final AnyController controller = new AnyController(validator);
		doThrow(new ValidationFailedException("")).when(messages).assertAbsenceOfErrors();
		when(methodInfo.getParametersValues()).thenReturn(new Object[0]);

		observer.execute(new InterceptorsExecuted(method, controller));
	}

	@Test
	public void shouldThrowApplicationLogicExceptionIfItsACheckedException() throws Exception {
		Method method = AnyController.class.getDeclaredMethod("throwException");
		ControllerMethod controllerMethod = instanceFor(AnyController.class, method);
		AnyController controller = new AnyController(validator);

		expected.expect(ApplicationLogicException.class);
		expected.expectCause(any(TestCheckedException.class));
		observer.execute(new InterceptorsExecuted(controllerMethod, controller));
		verify(messages).assertAbsenceOfErrors();
	}

	@Test
	public void shouldThrowTheBusinessExceptionIfItsUnChecked() throws Exception {
		Method method = AnyController.class.getDeclaredMethod("throwUnCheckedException");
		ControllerMethod controllerMethod = instanceFor(AnyController.class, method);
		AnyController controller = new AnyController(validator);

		expected.expect(TestException.class);
		observer.execute(new InterceptorsExecuted(controllerMethod, controller));
		verify(messages).assertAbsenceOfErrors();
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
		public void throwException() throws Exception {
			throw new TestCheckedException();
		}
		public void throwUnCheckedException() {
			throw new TestException();
		}
	}

	private static class TestException extends RuntimeException {}
	private static class TestCheckedException extends Exception {}
}
