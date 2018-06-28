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

package br.com.caelum.vraptor.http.route;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;

/**
 * Those are more likely to be acceptance than unit tests.
 * @author guilherme silveira
 */
public class DefaultRouterTest {

	private Proxifier proxifier;
	private DefaultRouter router;
	private VRaptorRequest request;
	private ControllerMethod method;
	private Converters converters;
	private ParameterNameProvider nameProvider;
	private EncodingHandler encodingHandler;
	private CacheStore<Invocation,Route> cache;

	@Before
	public void setup() {
		this.request = new VRaptorRequest(mock(HttpServletRequest.class));
		this.proxifier = new JavassistProxifier();
		this.method = mock(ControllerMethod.class);
		this.converters = mock(Converters.class);
		this.encodingHandler = mock(EncodingHandler.class);
		this.nameProvider = new ParanamerNameProvider();
		this.cache = new DefaultCacheStore<>();

		router = new DefaultRouter(proxifier, new NoTypeFinder(), converters, nameProvider, 
				new JavaEvaluator(new DefaultReflectionProvider()), encodingHandler,cache);
	}

	@Test
	public void shouldThrowControllerNotFoundExceptionWhenNoRoutesMatchTheURI() throws Exception {
		Route route = mock(Route.class);
		when(route.canHandle(anyString())).thenReturn(false);
		when(route.getControllerMethod()).thenReturn(anyControllerMethod());

		router.add(route);

		try {
			router.parse("any uri", HttpMethod.DELETE, request);
			fail("ControllerNotFoundException is expected");
		} catch (ControllerNotFoundException e) {

		}
	}

	@Test
	public void shouldThrowMethodNotAllowedExceptionWhenNoRoutesMatchTheURIWithGivenHttpMethod() throws Exception {
		Route route = mock(Route.class);
		when(route.canHandle(anyString())).thenReturn(true);
		when(route.getControllerMethod()).thenReturn(anyControllerMethod());
		when(route.allowedMethods()).thenReturn(EnumSet.of(HttpMethod.GET));

		router.add(route);

		try {
			router.parse("any uri", HttpMethod.DELETE, request);
			fail("MethodNotAllowedException is expected");
		} catch (MethodNotAllowedException e) {
			assertThat(e.getAllowedMethods(), is((Set<HttpMethod>)EnumSet.of(HttpMethod.GET)));
		}
	}

	private DefaultControllerMethod anyControllerMethod() throws NoSuchMethodException {
		return new DefaultControllerMethod(new DefaultBeanClass(MyController.class), MyController.class.getMethod("customizedPath"));
	}

	@Test
	public void shouldObeyPriorityOfRoutes() throws Exception {
		Route first = mock(Route.class);
		when(first.getControllerMethod()).thenReturn(anyControllerMethod());
		Route second = mock(Route.class);
		when(second.getControllerMethod()).thenReturn(anyControllerMethod());

		ControllerMethod method2 = second.controllerMethod(request, "second");

		router.add(second);
		router.add(first);

		when(first.getPriority()).thenReturn(Path.HIGH);
		when(second.getPriority()).thenReturn(Path.LOW);

		EnumSet<HttpMethod> get = EnumSet.of(HttpMethod.GET);
		when(first.allowedMethods()).thenReturn(get);
		when(second.allowedMethods()).thenReturn(get);

		when(first.canHandle(anyString())).thenReturn(false);
		when(second.canHandle(anyString())).thenReturn(true);

		ControllerMethod found = router.parse("anything", HttpMethod.GET, request);
		assertThat(found, is(method2));
	}

	@Test
	public void acceptsASingleMappingRule() throws SecurityException, NoSuchMethodException {
		Route route = mock(Route.class);
		when(route.getControllerMethod()).thenReturn(anyControllerMethod());

		when(route.canHandle("/clients/add")).thenReturn(true);
		when(route.allowedMethods()).thenReturn(EnumSet.of(HttpMethod.POST));
		when(route.controllerMethod(request, "/clients/add")).thenReturn(method);

		router.add(route);
		ControllerMethod found = router.parse("/clients/add", HttpMethod.POST, request);

		assertThat(found, is(equalTo(method)));
		verify(route, atLeastOnce()).getPriority();
	}


	@Test
	public void passesTheWebMethod() throws SecurityException, NoSuchMethodException {
		HttpMethod delete = HttpMethod.DELETE;
		Route route = mock(Route.class);
		when(route.getControllerMethod()).thenReturn(anyControllerMethod());

		when(route.canHandle("/clients/add")).thenReturn(true);
		when(route.allowedMethods()).thenReturn(EnumSet.of(delete));
		when(route.controllerMethod(request, "/clients/add")).thenReturn(method);

		router.add(route);
		ControllerMethod found = router.parse("/clients/add", delete, request);
		assertThat(found, is(equalTo(method)));
		verify(route, atLeastOnce()).getPriority();
	}

	@Test
	public void usesTheFirstRegisteredRuleMatchingThePattern() throws SecurityException, NoSuchMethodException {
		Route route = mock(Route.class);
		Route second = mock(Route.class, "second");
		when(route.getControllerMethod()).thenReturn(anyControllerMethod());
		when(second.getControllerMethod()).thenReturn(anyControllerMethod());

		when(route.canHandle("/clients/add")).thenReturn(true);
		when(second.canHandle("/clients/add")).thenReturn(true);

		EnumSet<HttpMethod> all = EnumSet.allOf(HttpMethod.class);

		when(route.allowedMethods()).thenReturn(all);
		when(second.allowedMethods()).thenReturn(all);

		when(route.controllerMethod(request, "/clients/add")).thenReturn(method);
		when(route.getPriority()).thenReturn(Path.HIGHEST);
		when(second.getPriority()).thenReturn(Path.LOWEST);

		router.add(route);
		router.add(second);

		ControllerMethod found = router.parse("/clients/add", HttpMethod.POST, request);
		assertThat(found, is(equalTo(method)));
	}
	@Test
	public void throwsExceptionIfMoreThanOneUriMatchesWithSamePriority() throws NoSuchMethodException {
		Route route = mock(Route.class);
		Route second = mock(Route.class, "second");
		when(route.getControllerMethod()).thenReturn(anyControllerMethod());
		when(second.getControllerMethod()).thenReturn(anyControllerMethod());

		when(route.canHandle("/clients/add")).thenReturn(true);
		when(second.canHandle("/clients/add")).thenReturn(true);

		EnumSet<HttpMethod> all = EnumSet.allOf(HttpMethod.class);

		when(route.allowedMethods()).thenReturn(all);
		when(second.allowedMethods()).thenReturn(all);

		when(route.getPriority()).thenReturn(Path.DEFAULT);
		when(second.getPriority()).thenReturn(Path.DEFAULT);

		router.add(route);
		router.add(second);

		try {
			router.parse("/clients/add", HttpMethod.POST, request);
			fail("IllegalStateException expected");
		} catch (IllegalStateException e) {
		}
	}

	@Test
	public void acceptsAnHttpMethodLimitedMappingRule() throws NoSuchMethodException {
		new Rules(router) {
			@Override
			public void routes() {
				routeFor("/clients/add").with(HttpMethod.POST).is(SomeController.class).add(null);
			}
		};
		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.controllerMethod(method(
				"add", Dog.class))));
	}

	private Method method(String methodName, Class<?>... params) throws SecurityException, NoSuchMethodException {
		return SomeController.class.getDeclaredMethod(methodName, params);
	}

	@Test
	public void acceptsAnHttpMethodLimitedMappingRuleWithBothMethods() throws NoSuchMethodException {
		new Rules(router) {
			@Override
			public void routes() {
				routeFor("/clients/add").with(HttpMethod.POST).with(HttpMethod.GET).is(SomeController.class).add(null);
			}
		};

		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.controllerMethod(method(
				"add", Dog.class))));

		assertThat(router.parse("/clients/add", HttpMethod.GET, request), is(VRaptorMatchers.controllerMethod(method(
				"add", Dog.class))));
	}

	@Test
	public void shouldReturnTheFirstRouteFound() throws Exception {
		Method method = MyController.class.getDeclaredMethod("listDogs", Integer.class);
		registerRulesFor(MyController.class);

		assertEquals("/dogs/1", router.urlFor(MyController.class, method, new Object[] { "1" }));
	}


	class Dog {
		private Long id;

		public void setId(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}
	}

	@Controller
	public static class SomeController {
		public void add(Dog object) {
		}

		public void unknownMethod() {
		}

		public void list() {
		}

		public void show(Dog dog) {
		}
	}

	@Controller
	public static class MyController {
		public void notAnnotated() {
		}

		@Path("/myPath")
		public void customizedPath() {
		}

		@Path("/*/customPath")
		public void starPath() {
		}

		@Get({ "/dogs/{page}", "/dogs" })
		public void listDogs(Integer page) {
		}
	}

	@Controller
	class InheritanceExample extends MyController {
	}

	@Test
	public void usesAsteriskBothWays() throws NoSuchMethodException {
		registerRulesFor(MyController.class);
		final Method method = MyController.class.getMethod("starPath");
		String url = router.urlFor(MyController.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
	}

	@Test
	public void shouldCacheInvocationsAfterFirstCall() throws NoSuchMethodException {
		registerRulesFor(MyController.class);
		final Method method = MyController.class.getMethod("starPath");
		router.urlFor(MyController.class, method, new Object[] {});
		String url = router.urlFor(MyController.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
	}

	private void registerRulesFor(Class<?> type) {
		RoutesParser parser = new PathAnnotationRoutesParser(router, new DefaultReflectionProvider());

		BeanClass controllerClass = new DefaultBeanClass(type);
		List<Route> rules = parser.rulesFor(controllerClass);
		for (Route route : rules) {
			router.add(route);
		}
	}

	@Test
	public void canTranslateAInheritedControllerBothWays() throws NoSuchMethodException {
		registerRulesFor(MyController.class);
		registerRulesFor(InheritanceExample.class);
		final Method method = MyController.class.getMethod("notAnnotated");
		String url = router.urlFor(InheritanceExample.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
	}

	@Test
	public void canTranslateAnnotatedMethodBothWays() throws NoSuchMethodException {
		registerRulesFor(MyController.class);
		final Method method = MyController.class.getMethod("customizedPath");
		String url = router.urlFor(MyController.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
	}

	@Test
	public void canFindUrlForProxyClasses() throws Exception {
		registerRulesFor(MyController.class);
		MyController proxy = proxifier.proxify(MyController.class, null);
		Class<? extends MyController> type = proxy.getClass();
		Method method = type.getMethod("notAnnotated");
		assertEquals("/my/notAnnotated", router.urlFor(type, method));
	}
}

class MyCustomController {
	public void notAnnotated() {
	}
}
