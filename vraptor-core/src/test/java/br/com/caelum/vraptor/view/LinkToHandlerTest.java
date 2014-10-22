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
package br.com.caelum.vraptor.view;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.ServletContext;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.exception.MirrorException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;

public class LinkToHandlerTest {
	private @Mock ServletContext context;
	private @Mock Router router;
	private LinkToHandler handler;
	private Method method2params;
	private Method method1param;
	private Method anotherMethod;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.handler = new LinkToHandler(context, router, new JavassistProxifier(), new DefaultReflectionProvider());
		when(context.getContextPath()).thenReturn("/path");

		this.method2params = new Mirror().on(TestController.class).reflect().method("method").withArgs(String.class, int.class);
		this.method1param = new Mirror().on(TestController.class).reflect().method("method").withArgs(String.class);
		this.anotherMethod = new Mirror().on(TestController.class).reflect().method("anotherMethod").withArgs(String.class, int.class);
	}

	@Test
	public void shouldThrowExceptionWhenMethodIsAmbiguous() throws Throwable {
		try {
			//${linkTo[TestController].method()}
			invoke(handler.get(new DefaultBeanClass(TestController.class)), "method");
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage().toLowerCase(), startsWith("ambiguous method"));
		}
	}

	@Test
	public void shouldThrowExceptionWhenUsingParametersOfWrongTypes() throws Throwable {
		//${linkTo[TestController].method(123)}
		try {
			invoke(handler.get(new DefaultBeanClass(TestController.class)), "method", 123);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage().toLowerCase(), startsWith("there are no methods"));
		}
	}


	@Test
	public void shouldReturnWantedUrlWithoutArgs() throws Throwable {
		when(router.urlFor(TestController.class, anotherMethod, new Object[2])).thenReturn("/expectedURL");

		//${linkTo[TestController].anotherMethod()}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "anotherMethod");
		assertThat(uri, is("/path/expectedURL"));
	}

	@Test
	public void shouldReturnWantedUrlWithoutArgsUsingPropertyAccess() throws Throwable {
		when(router.urlFor(TestController.class, anotherMethod, new Object[2])).thenReturn("/expectedURL");

		//${linkTo[TestController].anotherMethod}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "getAnotherMethod");
		assertThat(uri, is("/path/expectedURL"));
	}

	@Test
	public void shouldReturnWantedUrlWithParamArgs() throws Throwable {
		String a = "test";
		int b = 3;
		when(router.urlFor(TestController.class, method2params, new Object[]{a, b})).thenReturn("/expectedURL");
		//${linkTo[TestController].method('test', 3)}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "method", a, b);
		assertThat(uri, is("/path/expectedURL"));
	}

	@Test
	public void shouldReturnWantedUrlWithPartialParamArgs() throws Throwable {
		String a = "test";
		when(router.urlFor(TestController.class, anotherMethod, new Object[]{a, null})).thenReturn("/expectedUrl");
		//${linkTo[TestController].anotherMethod('test')}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "anotherMethod", a);
		assertThat(uri, is("/path/expectedUrl"));
	}

	@Test
	public void shouldReturnWantedUrlForOverrideMethodWithParamArgs() throws Throwable {
		String a = "test";
		when(router.urlFor(SubGenericController.class, SubGenericController.class.getDeclaredMethod("method", new Class[]{String.class}), new Object[]{a})).thenReturn("/expectedURL");
		//${linkTo[TestSubGenericController].method('test')}]
		String uri = invoke(handler.get(new DefaultBeanClass(SubGenericController.class)), "method", a);
		assertThat(uri, is("/path/expectedURL"));
	}

	@Test
	public void shouldReturnWantedUrlForOverrideMethodWithParialParamArgs() throws Throwable {
		String a = "test";
		when(router.urlFor(SubGenericController.class, SubGenericController.class.getDeclaredMethod("anotherMethod", new Class[]{String.class, String.class}), new Object[]{a, null})).thenReturn("/expectedURL");
		//${linkTo[TestSubGenericController].anotherMethod('test')}]
		String uri = invoke(handler.get(new DefaultBeanClass(SubGenericController.class)), "anotherMethod", a);
		assertThat(uri, is("/path/expectedURL"));
	}

	@Test
	public void shouldUseExactlyMatchedMethodIfTheMethodIsOverloaded() throws Throwable {
		String a = "test";
		when(router.urlFor(TestController.class, method1param, a)).thenReturn("/expectedUrl");
		//${linkTo[TestController].method('test')}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "method", a);
		assertThat(uri, is("/path/expectedUrl"));
	}

	@Test
	public void shouldThrowExceptionWhenPassingMoreArgsThanMethodSupports() throws Throwable {
		String a = "test";
		int b = 3;
		String c = "anotherTest";
		//${linkTo[TestController].anotherMethod('test', 3, 'anotherTest')}
		try {
			invoke(handler.get(new DefaultBeanClass(TestController.class)), "anotherMethod", a, b, c);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage().toLowerCase(), startsWith("wrong number of arguments"));
		}
	}

	@Test
	public void shouldAvoidFrozenClassIfTwoVRaptorInstancesAreLoaded() throws Throwable {
		ServletContext context0 = Mockito.mock(ServletContext.class);
		ServletContext context1 = Mockito.mock(ServletContext.class);

		when(context0.getContextPath()).thenReturn("");
		when(context1.getContextPath()).thenReturn("/another");

		ReflectionProvider reflectionProvider = new DefaultReflectionProvider();
		Proxifier proxifier = new JavassistProxifier();

		LinkToHandler handler0 = new LinkToHandler(context0, router, proxifier, reflectionProvider);
		LinkToHandler handler1 = new LinkToHandler(context1, router, proxifier, reflectionProvider);

		Object object0 = handler0.get(new DefaultBeanClass(TestController.class));
		assertThat(object0.getClass().getName(), containsString("$linkTo_$$"));

		Object object1 = handler1.get(new DefaultBeanClass(TestController.class));
		assertThat(object1.getClass().getName(), containsString("$linkTo$another_$$"));
	}

	private String invoke(Object obj, String methodName, Object...args) throws Throwable {
		Class<?>[] types = extractTypes(args);
		
		try {
			Method method = null;
			for (int length = types.length; length >= 0; length--) {
				method = new Mirror().on(obj.getClass()).reflect().method(methodName)
					.withArgs(Arrays.copyOf(types, length));
				if (method != null) 
					break;
			}
			
			if (methodName.startsWith("get")) {
				return method.invoke(obj).toString();
			}
			return new Mirror().on(obj).invoke().method(method).withArgs(args).toString();
		} catch (MirrorException | InvocationTargetException e) {
			throw e.getCause() == null? e : e.getCause();
		}
	}

	private Class<?>[] extractTypes(Object... args) {
		Class<?>[] classes = new Class<?>[args.length];
		
		for (int i = 0; i < classes.length; i++) {
			classes[i] = args[i].getClass();
		}
		return classes;
	}

	static class TestController {
		void method(String a, int b) {

		}
		void method(String a) {

		}
		void anotherMethod(String a, int b) {

		}
	}
}
