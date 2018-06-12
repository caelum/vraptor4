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
package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultConvertersTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock private Container container;
	private DefaultConverters converters;

	@Before
	public void setup() {
		CacheStore<Class<?>, Class<? extends Converter<?>>> cache = new DefaultCacheStore<>();
		MockitoAnnotations.initMocks(this);
		this.converters = new DefaultConverters(container, cache);
	}

	@Test
	public void complainsIfNoConverterFound() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Unable to find converter for " + getClass().getName());
		
		converters.to(DefaultConvertersTest.class);
	}

	@Test
	public void convertingANonAnnotatedConverterEndsUpComplaining() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("The converter type " + WrongConverter.class.getName() + " should have the Convert annotation");

		converters.register(WrongConverter.class);
	}

	@Test
	public void shouldChooseConverterWithGreaterPriority() {
		converters.register(MyConverter.class);
		converters.register(MySecondConverter.class);

		when(container.instanceFor(MyConverter.class)).thenReturn(new MyConverter());
		when(container.instanceFor(MySecondConverter.class)).thenReturn(new MySecondConverter());

		Object converter = converters.to(MyData.class);
		assertThat(converter, instanceOf(MySecondConverter.class));
	}

	@Test
	public void shouldForbidConverterWithSamePriority() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage(String.format("Converter %s have same priority than %s", MyThirdConverter.class, MySecondConverter.class));

		converters.register(MySecondConverter.class);
		converters.register(MyThirdConverter.class);
	}

	@Test
	public void findCorrectConverterSubclassLoadFirst() {
		converters.register(MySubConverter.class);
		converters.register(MySecondConverter.class);

		when(container.instanceFor(MySubConverter.class)).thenReturn(new MySubConverter());
		when(container.instanceFor(MySecondConverter.class)).thenReturn(new MySecondConverter());

		Object converter = converters.to(MySubData.class);
		assertThat(converter, instanceOf(MySubConverter.class));

		converter = converters.to(MyData.class);
		assertThat(converter, instanceOf(MySecondConverter.class));

		converter = converters.to(MyOtherSubData.class);
		assertThat(converter, instanceOf(MySecondConverter.class));
	}

	@Test
	public void findCorrectConverterSubclassLoadSecond() {
		converters.register(MySecondConverter.class);
		converters.register(MySubConverter.class);

		when(container.instanceFor(MySubConverter.class)).thenReturn(new MySubConverter());
		when(container.instanceFor(MySecondConverter.class)).thenReturn(new MySecondConverter());

		Object converter = converters.to(MySubData.class);
		assertThat(converter, instanceOf(MySubConverter.class));

		converter = converters.to(MyData.class);
		assertThat(converter, instanceOf(MySecondConverter.class));

		converter = converters.to(MyOtherSubData.class);
		assertThat(converter, instanceOf(MySecondConverter.class));
	}

	class WrongConverter implements Converter<String> {

		@Override
		public String convert(String value, Class<? extends String> type) {
			return null;
		}
	}

	class MyData {
	}

	class MySubData extends MyData {
	}

	class MyOtherSubData extends MyData {
	}

	@Convert(MyData.class)
	@Priority(Interceptor.Priority.LIBRARY_BEFORE)
	private class MyConverter implements Converter<MyData> {
		@Override
		public MyData convert(String value, Class<? extends MyData> type) {
			return null;
		}
	}

	@Convert(MyData.class)
	@Priority(javax.interceptor.Interceptor.Priority.APPLICATION)
	private class MySecondConverter implements Converter<MyData> {
		@Override
		public MyData convert(String value, Class<? extends MyData> type) {
			return null;
		}
	}

	@Convert(MyData.class)
	@Priority(javax.interceptor.Interceptor.Priority.APPLICATION)
	private class MyThirdConverter implements Converter<MyData> {
		@Override
		public MyData convert(String value, Class<? extends MyData> type) {
			return null;
		}
	}

	@Convert(MySubData.class)
	@Priority(javax.interceptor.Interceptor.Priority.APPLICATION)
	private class MySubConverter implements Converter<MySubData> {
		@Override
		public MySubData convert(String value, Class<? extends MySubData> type) {
			return null;
		}
	}

	@Test
	public void registersAndUsesTheConverterInstaceForTheSpecifiedType() {
		converters.register(MyConverter.class);
		when(container.instanceFor(MyConverter.class)).thenReturn(new MyConverter());

		Converter<?> found = converters.to(MyData.class);
		assertThat(found.getClass(), is(typeCompatibleWith(MyConverter.class)));
	}

	@Test
	public void existsForWillReturnTrueForRegisteredConverters() throws Exception {
		converters.register(MyConverter.class);

		when(container.instanceFor(MyConverter.class)).thenReturn(new MyConverter());

		assertTrue(converters.existsFor(MyData.class));
	}

}
