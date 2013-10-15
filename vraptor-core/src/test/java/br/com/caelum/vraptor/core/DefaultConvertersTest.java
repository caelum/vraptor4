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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultConvertersTest {

	@Mock private Container container;
	private DefaultConverters converters;

	@Before
	public void setup() {
		CacheStore<Class<?>, Class<? extends Converter<?>>> cache = new DefaultCacheStore<>();
		MockitoAnnotations.initMocks(this);
		this.converters = new DefaultConverters(container, cache);
	}

	@Test(expected = IllegalStateException.class)
	public void complainsIfNoConverterFound() {
		converters.to(DefaultConvertersTest.class);
	}

	@Test(expected = IllegalStateException.class)
	public void convertingANonAnnotatedConverterEndsUpComplaining() {
		converters.register(WrongConverter.class);
	}

	class WrongConverter implements Converter<String> {

		@Override
		public String convert(String value, Class<? extends String> type) {
			return null;
		}
	}

	class MyData {
	}

	@Convert(MyData.class)
	class MyConverter implements Converter<MyData> {
		@Override
		public MyData convert(String value, Class<? extends MyData> type) {
			return null;
		}
	}

	@Convert(MyData.class)
	class MySecondConverter implements Converter<MyData> {
		@Override
		public MyData convert(String value, Class<? extends MyData> type) {
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
