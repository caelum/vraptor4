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

package br.com.caelum.vraptor.ioc;

import static br.com.caelum.vraptor.VRaptorMatchers.canHandle;
import static br.com.caelum.vraptor.VRaptorMatchers.hasOneCopyOf;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Named;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.converter.BooleanConverter;
import br.com.caelum.vraptor.converter.ByteConverter;
import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.converter.EnumConverter;
import br.com.caelum.vraptor.converter.IntegerConverter;
import br.com.caelum.vraptor.converter.LocaleBasedCalendarConverter;
import br.com.caelum.vraptor.converter.LocaleBasedDateConverter;
import br.com.caelum.vraptor.converter.LocaleBasedDoubleConverter;
import br.com.caelum.vraptor.converter.LocaleBasedFloatConverter;
import br.com.caelum.vraptor.converter.LocaleBasedPrimitiveDoubleConverter;
import br.com.caelum.vraptor.converter.LocaleBasedPrimitiveFloatConverter;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.PrimitiveBooleanConverter;
import br.com.caelum.vraptor.converter.PrimitiveByteConverter;
import br.com.caelum.vraptor.converter.PrimitiveIntConverter;
import br.com.caelum.vraptor.converter.PrimitiveLongConverter;
import br.com.caelum.vraptor.converter.PrimitiveShortConverter;
import br.com.caelum.vraptor.converter.ShortConverter;
import br.com.caelum.vraptor.converter.jodatime.LocalDateConverter;
import br.com.caelum.vraptor.converter.jodatime.LocalTimeConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializers;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.cdi.CDIBasedContainer;
import br.com.caelum.vraptor.ioc.cdi.Code;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath.Provided;
import br.com.caelum.vraptor.ioc.fixture.ControllerInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ConverterInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.DependentOnSomethingFromComponentFactory;
import br.com.caelum.vraptor.ioc.fixture.InterceptorInTheClasspath;

/**
 * Acceptance test that checks if the container is capable of giving all
 * required components.
 *
 * @author Guilherme Silveira
 */
public abstract class GenericContainerTest {

	protected ContainerProvider provider;
	private Container currentContainer;

	protected abstract ContainerProvider getProvider();
	protected abstract <T> T executeInsideRequest(WhatToDo<T> execution);

	@Before
	public void setup() throws Exception {
		getStartedProvider();
		currentContainer = getCurrentContainer();
	}

	protected void getStartedProvider() {
		provider = getProvider();
		provider.start();
	}

	@After
	public void tearDown() {
		provider.stop();
		provider = null;
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

	@Dependent
	public static class MyDependentComponent {

	}

	@Test
	public void processesCorrectlyDependentComponents() {
		executeInsideRequest(new WhatToDo<Object>() {
			@Override
			public Object execute(RequestInfo request, int counter) {
				provider.provideForRequest(request);
				MyDependentComponent instance1 = instanceFor(MyDependentComponent.class, currentContainer);
				MyDependentComponent instance2 = instanceFor(MyDependentComponent.class, currentContainer);
				assertThat(instance1, not(sameInstance(instance2)));
				return null;
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
				provider.provideForRequest(request);
				ControllerMethod secondMethod = mock(ControllerMethod.class, "rm" + counter);
				Container secondContainer = currentContainer;
				secondContainer.instanceFor(MethodInfo.class).setControllerMethod(secondMethod);
				return instanceFor(component, secondContainer);
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
		provider.provideForRequest(request);
		return instanceFor(componentToBeRetrieved, currentContainer);
	}

	private CDIBasedContainer getCurrentContainer() {
		return CDI.current().select(CDIBasedContainer.class).get();
	}

	protected <T> T getFromContainerInCurrentThread(final Class<T> componentToBeRetrieved, RequestInfo request,final Code<T> code) {
		provider.provideForRequest(request);
		T bean = instanceFor(componentToBeRetrieved, currentContainer);
		code.execute(bean);
		return bean;
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
				provider.provideForRequest(request);
				Converters converters = currentContainer.instanceFor(Converters.class);
				Converter<?> converter = converters.to(Void.class);
				assertThat(converter, is(instanceOf(ConverterInTheClasspath.class)));
				return null;
			}
		});
	}

	/**
	 * Check if exist {@link Deserializer} registered in VRaptor for determined Content-Types.
	 */
	@Test
	public void shouldReturnAllDefaultDeserializers() {

		executeInsideRequest(new WhatToDo<Void>(){

			@Override
			public Void execute(RequestInfo request, int counter) {

				provider.provideForRequest(request);
				Deserializers deserializers = currentContainer.instanceFor(Deserializers.class);
				List<String> types = asList("application/json", "json", "application/xml",
					"xml", "text/xml", "application/x-www-form-urlencoded");

				for (String type : types) {
					assertThat("deserializer not found: " + type,
							deserializers.deserializerFor(type, currentContainer), is(notNullValue()));
				}
				return null;
			}
		});
	}

	@Test
	public void shouldReturnAllDefaultConverters() {
		executeInsideRequest(new WhatToDo<Void>(){
			@Override
			public Void execute(RequestInfo request, int counter) {
				provider.provideForRequest(request);

				Converters converters = currentContainer.instanceFor(Converters.class);

				final HashMap<Class<?>, Class<?>> EXPECTED_CONVERTERS = new HashMap<Class<?>, Class<?>>() {
					{
						put(int.class, PrimitiveIntConverter.class);
						put(long.class, PrimitiveLongConverter.class);
						put(short.class, PrimitiveShortConverter.class);
						put(byte.class, PrimitiveByteConverter.class);
						put(double.class, LocaleBasedPrimitiveDoubleConverter.class);
						put(float.class, LocaleBasedPrimitiveFloatConverter.class);
						put(boolean.class, PrimitiveBooleanConverter.class);
						put(Integer.class, IntegerConverter.class);
						put(Long.class, LongConverter.class);
						put(Short.class, ShortConverter.class);
						put(Byte.class, ByteConverter.class);
						put(Double.class, LocaleBasedDoubleConverter.class);
						put(Float.class, LocaleBasedFloatConverter.class);
						put(Boolean.class, BooleanConverter.class);
						put(Calendar.class, LocaleBasedCalendarConverter.class);
						put(Date.class, LocaleBasedDateConverter.class);
						put(Enum.class, EnumConverter.class);
					}
					private static final long serialVersionUID = 8559316558416038474L;
				};

				for (Entry<Class<?>, Class<?>> entry : EXPECTED_CONVERTERS.entrySet()) {
					Converter<?> converter = converters.to((Class<?>) entry.getKey());
					assertThat(converter, is(instanceOf(entry.getValue())));
				}
				return null;
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

	protected <T> T instanceFor(final Class<T> component, Container container) {
		return container.instanceFor(component);
	}
}
