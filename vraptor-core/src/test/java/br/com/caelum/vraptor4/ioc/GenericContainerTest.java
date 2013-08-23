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

package br.com.caelum.vraptor4.ioc;

import static br.com.caelum.vraptor4.VRaptorMatchers.canHandle;
import static br.com.caelum.vraptor4.VRaptorMatchers.hasOneCopyOf;
import static br.com.caelum.vraptor4.config.BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME;
import static br.com.caelum.vraptor4.config.BasicConfiguration.SCANNING_PARAM;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor4.Converter;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.converter.jodatime.LocalDateConverter;
import br.com.caelum.vraptor4.converter.jodatime.LocalTimeConverter;
import br.com.caelum.vraptor4.core.BaseComponents;
import br.com.caelum.vraptor4.core.Converters;
import br.com.caelum.vraptor4.core.Execution;
import br.com.caelum.vraptor4.core.MethodInfo;
import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.deserialization.Deserializer;
import br.com.caelum.vraptor4.deserialization.Deserializers;
import br.com.caelum.vraptor4.http.route.Route;
import br.com.caelum.vraptor4.http.route.Router;
import br.com.caelum.vraptor4.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor4.ioc.cdi.Code;
import br.com.caelum.vraptor4.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.ComponentFactoryInTheClasspath.Provided;
import br.com.caelum.vraptor4.ioc.fixture.ControllerInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.ConverterInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.CustomComponentInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.DependentOnSomethingFromComponentFactory;
import br.com.caelum.vraptor4.ioc.fixture.InterceptorInTheClasspath;

/**
 * Acceptance test that checks if the container is capable of giving all
 * required components.
 *
 * @author Guilherme Silveira
 */
public abstract class GenericContainerTest {

	protected ContainerProvider provider;
	protected ServletContext context;
	private static final String PACKAGENAME = "br.com.caelum.vraptor4.ioc.fixture";

	protected abstract ContainerProvider getProvider();
	protected abstract <T> T executeInsideRequest(WhatToDo<T> execution);
	protected abstract void configureExpectations();

	@Before
	public void setup() throws Exception {

		ClassLoader contextClassLoader = currentThread().getContextClassLoader();
		URL[] urls = new URL[] {Object.class.getResource("/test-fixture.jar")};

		context = mock(ServletContext.class, "servlet context");
		when(context.getMajorVersion()).thenReturn(3);
		when(context.getInitParameter(BASE_PACKAGES_PARAMETER_NAME)).thenReturn(PACKAGENAME);
		when(context.getRealPath("/WEB-INF/classes")).thenReturn(getClassDir());
		when(context.getClassLoader()).thenReturn(new URLClassLoader(urls, contextClassLoader));
        when(context.getInitParameter(SCANNING_PARAM)).thenReturn("enabled");

		configureExpectations();
		getStartedProvider();
	}

	protected void getStartedProvider() {
		provider = getProvider();
		provider.start(context);
	}

	@After
	public void tearDown() {
		provider.stop();
		provider = null;
	}

	@Test
	public void canProvideAllApplicationScopedComponents() {
		checkAvailabilityFor(true, BaseComponents.getApplicationScoped().keySet());
	}

	@Test
	public void canProvideAllPrototypeScopedComponents() {
		checkAvailabilityFor(false, BaseComponents.getPrototypeScoped().keySet());
	}

	@Test
	public void canProvideAllRequestScopedComponents() {
		checkAvailabilityFor(false, BaseComponents.getRequestScoped().keySet());
	}

	@ApplicationScoped
	public static class MyAppComponent {

	}

	@Test
	public void processesCorrectlyAppBasedComponents() {
		checkAvailabilityFor(true, MyAppComponent.class);
	}

	@Test
	public void canProvideJodaTimeConverters() {
		executeInsideRequest(new WhatToDo<String>() {
			@Override
			public String execute(RequestInfo request, int counter) {
				assertNotNull(getFromContainerInCurrentThread(LocalDateConverter.class, request));
				assertNotNull(getFromContainerInCurrentThread(LocalTimeConverter.class, request));
				Converters converters = getFromContainerInCurrentThread(Converters.class, request);
				assertTrue(converters.existsFor(LocalDate.class));
				assertTrue(converters.existsFor(LocalTime.class));
				return null;
			}
		});
	}

	@ApplicationScoped
	public static class MyAppComponentWithLifecycle {
		private int calls = 0;

		public int getCalls() {
			return calls;
		}

		@PreDestroy
		public void z() {
			calls++;
		}
	}

	@Test
	public void callsPredestroyExactlyOneTime() throws Exception {
		MyAppComponentWithLifecycle component = getFromContainer(MyAppComponentWithLifecycle.class);
		assertThat(component.calls, is(0));
		provider.stop();
		assertThat(component.calls, is(1));
		getStartedProvider();
	}

	@RequestScoped
	@Named("teste")
	public static class MyRequestComponent {

	}

	@Test
	public void processesCorrectlyRequestBasedComponents() {
		checkAvailabilityFor(false, MyRequestComponent.class);
	}

	@PrototypeScoped
	public static class MyPrototypeComponent {

	}

	@Test
	public void processesCorrectlyPrototypeBasedComponents() {
		executeInsideRequest(new WhatToDo<Object>() {
			@Override
			public Object execute(RequestInfo request, int counter) {
				return provider.provideForRequest(request, new Execution<Object>() {
					@Override
					public Object insideRequest(Container container) {
						MyPrototypeComponent instance1 = instanceFor(MyPrototypeComponent.class,container);
						MyPrototypeComponent instance2 = instanceFor(MyPrototypeComponent.class,container);
						assertThat(instance1, not(sameInstance(instance2)));
						return null;
					}
				});
			}
		});
	}

	@Test
	public void supportsComponentFactoriesForCustomInstantiation() {
		TheComponentFactory factory = getFromContainer(TheComponentFactory.class);
		assertThat(factory, is(notNullValue()));

		NeedsCustomInstantiation component = getFromContainer(NeedsCustomInstantiation.class);
		assertThat(component, is(notNullValue()));

		DependentOnSomethingFromComponentFactory dependent =
				getFromContainer(DependentOnSomethingFromComponentFactory.class);

		assertThat(dependent, is(notNullValue()));
		assertThat(dependent.getDependency(), is(notNullValue()));
	}

	protected <T> void checkAvailabilityFor(final boolean shouldBeTheSame, final Class<T> component) {
		T firstInstance = getFromContainer(component);
		T secondInstance = executeInsideRequest(new WhatToDo<T>() {
			@Override
			public T execute(RequestInfo request, final int counter) {
				return provider.provideForRequest(request, new Execution<T>() {
					@Override
					public T insideRequest(Container secondContainer) {
						ControllerMethod secondMethod = mock(ControllerMethod.class, "rm" + counter);
						secondContainer.instanceFor(MethodInfo.class).setControllerMethod(secondMethod);
						return instanceFor(component, secondContainer);
					}
				});

			}
		});

		checkSimilarity(component, shouldBeTheSame, firstInstance, secondInstance);
	}

	protected <T> T getFromContainer(final Class<T> componentToBeRetrieved) {
		return executeInsideRequest(new WhatToDo<T>() {
			@Override
			public T execute(RequestInfo request, final int counter) {
				return getFromContainerInCurrentThread(componentToBeRetrieved, request);
			}
		});
	}

	protected <T> T getFromContainerAndExecuteSomeCode(final Class<T> componentToBeRetrieved,final Code<T> code) {
		return executeInsideRequest(new WhatToDo<T>() {
			@Override
			public T execute(RequestInfo request, final int counter) {
				T bean = getFromContainerInCurrentThread(componentToBeRetrieved, request,code);
				return bean;
			}
		});
	}

	protected <T> T getFromContainerInCurrentThread(final Class<T> componentToBeRetrieved, RequestInfo request) {
		return provider.provideForRequest(request, new Execution<T>() {
			@Override
			public T insideRequest(Container firstContainer) {
				return instanceFor(componentToBeRetrieved,firstContainer);
			}
		});
	}
	protected <T> T getFromContainerInCurrentThread(final Class<T> componentToBeRetrieved, RequestInfo request,final Code<T> code) {
		return provider.provideForRequest(request, new Execution<T>() {
			@Override
			public T insideRequest(Container firstContainer) {
				T bean = instanceFor(componentToBeRetrieved,firstContainer);
				code.execute(bean);
				return bean;
			}
		});
	}

	protected void checkSimilarity(Class<?> component, boolean shouldBeTheSame, Object firstInstance,
			Object secondInstance) {

		if (shouldBeTheSame) {
			MatcherAssert.assertThat("Should be the same instance for " + component.getName(), firstInstance,
					is(equalTo(secondInstance)));
		} else {
			MatcherAssert.assertThat("Should not be the same instance for " + component.getName(), firstInstance,
					is(not(equalTo(secondInstance))));
		}
	}

	protected void checkAvailabilityFor(boolean shouldBeTheSame, Collection<Class<?>> components) {
		for (Class<?> component : components) {
			checkAvailabilityFor(shouldBeTheSame, component);
		}
	}

	@RequestScoped
	static public class DisposableComponent {
		private boolean destroyed;
		private final Object dependency = new Object();

		public Object getDependency() {
			return dependency;
		}

		@PreDestroy
		public void preDestroy() {
			this.destroyed = true;
		}

		public boolean isDestroyed() {
			return destroyed;
		}
	}

	static public class StartableComponent {
		private boolean started;

		@PostConstruct
		public void postConstruct() {
			this.started = true;
		}
	}

	@Test
	public void shouldDisposeAfterRequest() {
		DisposableComponent comp = getFromContainer(DisposableComponent.class);
		assertTrue(comp.destroyed);
	}

	@Test
	public void shouldStartBeforeRequestExecution() {
		StartableComponent comp = getFromContainer(StartableComponent.class);
		assertTrue(comp.started);
	}

	@Test
	public void canProvideComponentsInTheClasspath() throws Exception {
		checkAvailabilityFor(false, Collections.<Class<?>> singleton(CustomComponentInTheClasspath.class));
	}

	@Test
	public void shoudRegisterResourcesInRouter() {
		Router router = getFromContainer(Router.class);
		Matcher<Iterable<? super Route>> hasItem = hasItem(canHandle(ControllerInTheClasspath.class,
				ControllerInTheClasspath.class.getDeclaredMethods()[0]));
		assertThat(router.allRoutes(), hasItem);
	}

	@Test
	public void shoudUseComponentFactoriesInTheClasspath() {
		Provided object = getFromContainer(Provided.class);
		assertThat(object, is(sameInstance(ComponentFactoryInTheClasspath.PROVIDED)));
	}

	@Test
	public void shoudRegisterConvertersInConverters() {
		executeInsideRequest(new WhatToDo<Converters>() {
			@Override
			public Converters execute(RequestInfo request, final int counter) {
				return provider.provideForRequest(request, new Execution<Converters>() {
					@Override
					public Converters insideRequest(Container container) {
						Converters converters = container.instanceFor(Converters.class);
						Converter<?> converter = converters.to(Void.class);
						assertThat(converter, is(instanceOf(ConverterInTheClasspath.class)));
						return null;
					}
				});
			}
		});
	}

	/**
	 * Check if exist {@link Deserializer} registered in VRaptor for determined Content-Types.
	 */
	@Test
	public void shouldReturnAllDefaultDeserializers() {

		executeInsideRequest(new WhatToDo<Void>(){

			public Void execute(RequestInfo request, int counter) {

				return provider.provideForRequest(request, new Execution<Void>() {

					public Void insideRequest(Container container) {
						Deserializers deserializers = container.instanceFor(Deserializers.class);

						List<String> types = asList("application/json", "json", "application/xml",
							"xml", "text/xml", "application/x-www-form-urlencoded");

						for (String type : types) {
							assertThat(deserializers.deserializerFor(type, container), is(notNullValue()));
						}
						return null;
					}
				});
			}
		});
	}

	@Test
	public void shoudRegisterInterceptorsInInterceptorRegistry() {
		InterceptorRegistry registry = getFromContainer(InterceptorRegistry.class);
		assertThat(registry.all(), hasOneCopyOf(InterceptorInTheClasspath.class));
	}

	@Test
	public void shoudCallPredestroyExactlyOneTimeForComponentsScannedFromTheClasspath() {
		CustomComponentWithLifecycleInTheClasspath component = getFromContainer(CustomComponentWithLifecycleInTheClasspath.class);
		assertThat(component.getCallsToPreDestroy(), is(equalTo(0)));
		provider.stop();
		assertThat(component.getCallsToPreDestroy(), is(equalTo(1)));
		getStartedProvider();
	}

	@Test
	public void shoudCallPredestroyExactlyOneTimeForComponentFactoriesScannedFromTheClasspath() {
		ComponentFactoryInTheClasspath componentFactory = getFromContainer(ComponentFactoryInTheClasspath.class);
		assertThat(componentFactory.getCallsToPreDestroy(), is(equalTo(0)));
		provider.stop();
		assertThat(componentFactory.getCallsToPreDestroy(), is(equalTo(1)));
		getStartedProvider();
	}

	protected String getClassDir() {
		return getClass().getResource("/br/com/caelum/vraptor4/test").getFile();
	}

	protected <T> T instanceFor(final Class<T> component, Container container) {
		return container.instanceFor(component);
	}

}
