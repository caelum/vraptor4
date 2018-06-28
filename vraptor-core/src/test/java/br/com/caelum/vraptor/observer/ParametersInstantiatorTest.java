/***
 * Copyright (c) 2009 Caelum - wwyhiw.caelum.com.br/opensource
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

import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.caelum.vraptor.HeaderParam;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.InterceptorsReady;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.Validator;
import br.com.caelum.vraptor.view.FlashScope;

public class ParametersInstantiatorTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private MethodInfo methodInfo = new MethodInfo(new ParanamerNameProvider());
	private @Mock ParametersProvider parametersProvider;
	private @Mock Validator validator;
	private @Mock ResourceBundle bundle;
	private @Mock MutableRequest request;
	private @Mock FlashScope flash;

	private List<Message> errors ;
	private ParametersInstantiator instantiator;

	private ControllerMethod method;
	private ControllerMethod otherMethod;

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(request.getParameterNames()).thenReturn(Collections.<String> emptyEnumeration());

		this.instantiator = new ParametersInstantiator(parametersProvider, methodInfo, validator, request, flash);

		this.errors = (List<Message>) new Mirror().on(instantiator).get().field("errors");
		this.method = DefaultControllerMethod.instanceFor(Component.class, Component.class.getDeclaredMethod("method"));
		this.otherMethod = DefaultControllerMethod.instanceFor(Component.class, Component.class.getDeclaredMethod("otherMethod", int.class));
	}

	class Component {
		void method() {
		}
		void otherMethod(int oneParam){
		}
	}

	class HeaderParamComponent{
		void method(@HeaderParam("X-MyApp-Password") String password) {}
		void otherMethod(@HeaderParam("X-MyApp-User") String user,@HeaderParam("X-MyApp-Password") String password, @HeaderParam("X-MyApp-Token") String token) {}
	}

	@Test
	public void shouldNotAcceptIfMethodHasNoParameters() {
		methodInfo.setControllerMethod(method);

		verifyNoMoreInteractions(parametersProvider, validator, request, flash);
		instantiator.instantiate(new InterceptorsReady(method));
	}

	@Test
	public void shouldUseTheProvidedParameters() throws Exception {
		Object[] values = new Object[] { "bazinga" };
		methodInfo.setControllerMethod(otherMethod);

		when(parametersProvider.getParametersFor(otherMethod, errors)).thenReturn(values);

		instantiator.instantiate(new InterceptorsReady(otherMethod));

		verify(validator).addAll(Collections.<Message> emptyList());

		assertEquals("bazinga", methodInfo.getValuedParameters()[0].getValue());
	}

	@Test
	public void shouldConvertArrayParametersToIndexParameters() throws Exception {
		when(request.getParameterNames()).thenReturn(enumeration(asList("someParam[].id", "unrelatedParam")));
		when(request.getParameterValues("someParam[].id")).thenReturn(new String[] {"one", "two", "three"});
		when(parametersProvider.getParametersFor(otherMethod, errors)).thenReturn(new Object[1]);

		methodInfo.setControllerMethod(otherMethod);
		instantiator.instantiate(new InterceptorsReady(otherMethod));

		verify(request).setParameter("someParam[0].id", "one");
		verify(request).setParameter("someParam[1].id", "two");
		verify(request).setParameter("someParam[2].id", "three");
	}

	/**
	 * Bug related
	 */
	@Test
	public void shouldThrowExceptionWhenThereIsAParameterContainingDotClass() throws Exception {
		exception.expect(IllegalArgumentException.class);

		methodInfo.setControllerMethod(otherMethod);

		when(request.getParameterNames()).thenReturn(enumeration(asList("someParam.class.id", "unrelatedParam")));
		when(request.getParameterValues("someParam.class.id")).thenReturn(new String[] {"whatever"});

		instantiator.instantiate(new InterceptorsReady(otherMethod));
	}

	@Test
	public void shouldUseAndDiscardFlashParameters() throws Exception {
		Object[] values = new Object[] { "bazinga" };
		methodInfo.setControllerMethod(otherMethod);

		when(flash.consumeParameters(otherMethod)).thenReturn(values);

		instantiator.instantiate(new InterceptorsReady(otherMethod));

		verify(validator).addAll(Collections.<Message>emptyList());
		verify(parametersProvider, never()).getParametersFor(otherMethod, errors);

		assertEquals("bazinga", methodInfo.getValuedParameters()[0].getValue());
	}

	@Test
	public void shouldValidateParameters() throws Exception {
		methodInfo.setControllerMethod(otherMethod);

		when(parametersProvider.getParametersFor(otherMethod, errors))
			.thenAnswer(addErrorsToListAndReturn(new Object[] { 0 }, "error1"));

		instantiator.instantiate(new InterceptorsReady(otherMethod));

		verify(validator).addAll(errors);
		assertEquals(methodInfo.getValuedParameters()[0].getValue(), 0);
	}

	@Test
	public void shouldAddHeaderInformationToRequestWhenHeaderParamAnnotationIsPresent() throws Exception {
		Object[] values = new Object[] { "bazinga" };
		Method method = HeaderParamComponent.class.getDeclaredMethod("method", String.class);
		ControllerMethod controllerMethod = DefaultControllerMethod.instanceFor(HeaderParamComponent.class, method);
		methodInfo.setControllerMethod(controllerMethod);

		when(request.getHeader("X-MyApp-Password")).thenReturn("123");
		when(parametersProvider.getParametersFor(controllerMethod, errors)).thenReturn(values);

		instantiator.instantiate(new InterceptorsReady(controllerMethod));

		verify(request).setParameter("password", "123");
		verify(validator).addAll(Collections.<Message> emptyList());
	}
	
	@Test
	public void shouldNotAddHeaderInformationToRequestIfHeaderParamValueIsNull() throws Exception {
		Method method = HeaderParamComponent.class.getDeclaredMethod("method", String.class);
		ControllerMethod controllerMethod = DefaultControllerMethod.instanceFor(HeaderParamComponent.class, method);
		methodInfo.setControllerMethod(controllerMethod);

		when(request.getHeader("X-MyApp-Password")).thenReturn(null);
		when(parametersProvider.getParametersFor(controllerMethod, errors)).thenReturn(new Object[] { "" });

		instantiator.instantiate(new InterceptorsReady(controllerMethod));
		verify(request, never()).setParameter(anyString(), anyString());
	}

	@Test
	public void shouldNotAddHeaderInformationToRequestWhenHeaderParamAnnotationIsNotPresent() throws Exception {
		Object[] values = new Object[] { "bazinga" };
		when(parametersProvider.getParametersFor(otherMethod, errors)).thenReturn(values);

		methodInfo.setControllerMethod(otherMethod);
		instantiator.instantiate(new InterceptorsReady(otherMethod));

		verify(request, never()).setParameter(anyString(), anyString());
		verify(validator).addAll(Collections.<Message>emptyList());
	}
	
	private <T> Answer<T> addErrorsToListAndReturn(final T value, final String... messages) {
		return new Answer<T>() {
			@Override
			public T answer(InvocationOnMock invocation) throws Throwable {
				for (String message : messages) {
					errors.add(new SimpleMessage("test", message));
				}
				return value;
			}
		};
	}
}
