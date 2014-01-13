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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.CDI;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.converter.BooleanConverter;
import br.com.caelum.vraptor.converter.ByteConverter;
import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.converter.EnumConverter;
import br.com.caelum.vraptor.converter.IntegerConverter;
import br.com.caelum.vraptor.converter.CalendarConverter;
import br.com.caelum.vraptor.converter.DateConverter;
import br.com.caelum.vraptor.converter.DoubleConverter;
import br.com.caelum.vraptor.converter.FloatConverter;
import br.com.caelum.vraptor.converter.PrimitiveDoubleConverter;
import br.com.caelum.vraptor.converter.PrimitiveFloatConverter;
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
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.cdi.CDIBasedContainer;
import br.com.caelum.vraptor.ioc.fixture.ControllerInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ConverterInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.InterceptorInTheClasspath;
import br.com.caelum.vraptor.serialization.Deserializer;
import br.com.caelum.vraptor.serialization.Deserializers;

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

	@Test
	public void canProvideJodaTimeConverters() {
		assertNotNull(instanceFor(LocalDateConverter.class));
		assertNotNull(instanceFor(LocalTimeConverter.class));
		Converters converters = instanceFor(Converters.class);
		assertTrue(converters.existsFor(LocalDate.class));
		assertTrue(converters.existsFor(LocalTime.class));
	}

	private CDIBasedContainer getCurrentContainer() {
		return CDI.current().select(CDIBasedContainer.class).get();
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

	@Test
	public void shoudRegisterResourcesInRouter() {
		Router router = instanceFor(Router.class);
		Matcher<Iterable<? super Route>> hasItem = hasItem(canHandle(ControllerInTheClasspath.class,
				ControllerInTheClasspath.class.getDeclaredMethods()[0]));
		assertThat(router.allRoutes(), hasItem);
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
						put(double.class, PrimitiveDoubleConverter.class);
						put(float.class, PrimitiveFloatConverter.class);
						put(boolean.class, PrimitiveBooleanConverter.class);
						put(Integer.class, IntegerConverter.class);
						put(Long.class, LongConverter.class);
						put(Short.class, ShortConverter.class);
						put(Byte.class, ByteConverter.class);
						put(Double.class, DoubleConverter.class);
						put(Float.class, FloatConverter.class);
						put(Boolean.class, BooleanConverter.class);
						put(Calendar.class, CalendarConverter.class);
						put(Date.class, DateConverter.class);
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
		InterceptorRegistry registry = instanceFor(InterceptorRegistry.class);
		assertThat(registry.all(), hasOneCopyOf(InterceptorInTheClasspath.class));
	}

	protected <T> T instanceFor(final Class<T> component) {
		CDIBasedContainer container = getCurrentContainer();
		return container.instanceFor(component);
	}
}
