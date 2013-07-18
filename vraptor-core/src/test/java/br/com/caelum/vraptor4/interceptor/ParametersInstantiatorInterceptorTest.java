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

package br.com.caelum.vraptor4.interceptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.caelum.vraptor4.HeaderParam;
import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.Validator;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.core.Localization;
import br.com.caelum.vraptor4.core.MethodInfo;
import br.com.caelum.vraptor4.http.MutableRequest;
import br.com.caelum.vraptor4.http.ParameterNameProvider;
import br.com.caelum.vraptor4.http.ParametersProvider;
import br.com.caelum.vraptor4.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4.restfulie.controller.DefaultControllerMethod;
import br.com.caelum.vraptor4.validator.Message;
import br.com.caelum.vraptor4.validator.ValidationMessage;
import br.com.caelum.vraptor4.view.FlashScope;

public class ParametersInstantiatorInterceptorTest {

    private @Mock MethodInfo params;
    private @Mock ParametersProvider parametersProvider;
    private @Mock ParameterNameProvider parameterNameProvider;
	private @Mock Validator validator;
	private @Mock Localization localization;
	private @Mock InterceptorStack stack;
	private @Mock ResourceBundle bundle;
	private @Mock MutableRequest request;
	private @Mock FlashScope flash;

	private List<Message> errors ;
	private ParametersInstantiatorInterceptor instantiator;

	private ControllerMethod method;
	private ControllerMethod otherMethod;

	@Before
	@SuppressWarnings("unchecked")
    public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(localization.getBundle()).thenReturn(bundle);
		when(request.getParameterNames()).thenReturn(Collections.enumeration(Collections.EMPTY_LIST));

        this.instantiator = new ParametersInstantiatorInterceptor(parametersProvider, parameterNameProvider, params, validator, localization, request, flash);

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
    public void shouldAcceptIfMethodHasParameters() {
    	assertTrue(instantiator.accepts(otherMethod));
    }
    
    @Test
    public void shouldNotAcceptIfMethodHasNoParameters() {
    	assertFalse(instantiator.accepts(method));
    }

    @Test
    public void shouldUseTheProvidedParameters() throws InterceptionException, IOException, NoSuchMethodException {

    	Object[] values = new Object[] { new Object() };

    	when(parametersProvider.getParametersFor(method, errors, bundle)).thenReturn(values);

        instantiator.intercept(stack, method, null);

        verify(params).setParameters(values);
        verify(stack).next(method, null);
        verify(validator).addAll(Collections.<Message>emptyList());
    }

    @Test
    public void shouldConvertArrayParametersToIndexParameters() throws Exception {

    	when(request.getParameterNames()).thenReturn(Collections.enumeration(Arrays.asList("someParam[].id", "unrelatedParam")));
    	when(request.getParameterValues("someParam[].id")).thenReturn(new String[] {"one", "two", "three"});

    	instantiator.intercept(stack, method, null);

    	verify(request).setParameter("someParam[0].id", "one");
    	verify(request).setParameter("someParam[1].id", "two");
    	verify(request).setParameter("someParam[2].id", "three");
    }

    /**
     * Bug related
     */
    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowExceptionWhenThereIsAParameterContainingDotClass() throws Exception {

    	when(request.getParameterNames()).thenReturn(Collections.enumeration(Arrays.asList("someParam.class.id", "unrelatedParam")));
    	when(request.getParameterValues("someParam.class.id")).thenReturn(new String[] {"whatever"});

    	instantiator.intercept(stack, method, null);

    }
    
    @Test
    public void shouldUseAndDiscardFlashParameters() throws InterceptionException, IOException, NoSuchMethodException {
		Object[] values = new Object[] { new Object() };

		when(flash.consumeParameters(method)).thenReturn(values);

    	instantiator.intercept(stack, method, null);

    	verify(params).setParameters(values);
    	verify(stack).next(method, null);
    	verify(validator).addAll(Collections.<Message>emptyList());
    	verify(parametersProvider, never()).getParametersFor(method, errors, bundle);
    }

    @Test
    public void shouldValidateParameters() throws Exception {
    	Object[] values = new Object[]{0};

    	when(parametersProvider.getParametersFor(otherMethod, errors, bundle)).thenAnswer(addErrorsToListAndReturn(values, "error1"));

        instantiator.intercept(stack, otherMethod, null);

        verify(validator).addAll(errors);
        verify(stack).next(otherMethod, null);
        verify(params).setParameters(values);
    }

	@Test(expected=RuntimeException.class)
    public void shouldThrowException() throws Exception {

    	when(parametersProvider.getParametersFor(method, errors, bundle)).thenThrow(new RuntimeException());

        instantiator.intercept(stack, method, null);
    }
	
	@Test
	public void shouldAddHeaderInformationToRequestWhenHeaderParamAnnotationIsPresent() throws Exception {
		Object[] values = new Object[] { new Object() };
		Method method = HeaderParamComponent.class.getDeclaredMethod("method", String.class);
		ControllerMethod resourceMethod = DefaultControllerMethod.instanceFor(HeaderParamComponent.class, method);
		
		when(request.getHeader("X-MyApp-Password")).thenReturn("123");
    	when(parametersProvider.getParametersFor(resourceMethod, errors, bundle)).thenReturn(values);
    	when(parameterNameProvider.parameterNamesFor(method)).thenReturn(new String[]{"password"});

        instantiator.intercept(stack, resourceMethod, null);
        
		verify(request).setAttribute("password", "123");
        verify(params).setParameters(values);
        verify(stack).next(resourceMethod, null);
        verify(validator).addAll(Collections.<Message>emptyList());
	}
	
	@Test
	public void shouldAddHeaderInformationToRequestWhenHeaderParamAnnotationIsNotPresent() throws Exception {
		Object[] values = new Object[] { new Object() };
		Method method = Component.class.getDeclaredMethod("method");
		ControllerMethod resourceMethod = DefaultControllerMethod.instanceFor(Component.class, method);
		
    	when(parametersProvider.getParametersFor(resourceMethod, errors, bundle)).thenReturn(values);
    	when(parameterNameProvider.parameterNamesFor(method)).thenReturn(new String[]{"password"});

        instantiator.intercept(stack, resourceMethod, null);
        
        verify(request, never()).setAttribute(anyString(), anyString());
        verify(params).setParameters(values);
        verify(stack).next(resourceMethod, null);
        verify(validator).addAll(Collections.<Message>emptyList());
	}
	
	@Test
	public void shouldAddVariousHeaderInformationsToRequestWhenHeaderParamAnnotationIsPresent() throws Exception {
		Object[] values = new Object[] { new Object() };
		Method method = HeaderParamComponent.class.getDeclaredMethod("otherMethod", String.class, String.class, String.class);
		ControllerMethod resouceMethod = DefaultControllerMethod.instanceFor(HeaderParamComponent.class, method);
		
		when(request.getHeader("X-MyApp-User")).thenReturn("user");
		when(request.getHeader("X-MyApp-Password")).thenReturn("123");
		when(request.getHeader("X-MyApp-Token")).thenReturn("daek2321");
    	when(parametersProvider.getParametersFor(resouceMethod, errors, bundle)).thenReturn(values);
    	when(parameterNameProvider.parameterNamesFor(method)).thenReturn(new String[]{"user", "password", "token"});

        instantiator.intercept(stack, resouceMethod, null);
        
        verify(request).setAttribute("user", "user");
        verify(request).setAttribute("password", "123");
        verify(request).setAttribute("token", "daek2321");
        verify(params).setParameters(values);
        verify(stack).next(resouceMethod, null);
        verify(validator).addAll(Collections.<Message>emptyList());
	}

    private <T> Answer<T> addErrorsToListAndReturn(final T value, final String... messages) {
    	return new Answer<T>() {

			public T answer(InvocationOnMock invocation) throws Throwable {
				for (String message : messages) {
					errors.add(new ValidationMessage(message, "test"));
				}
				return value;
			}

		};

    }
}
