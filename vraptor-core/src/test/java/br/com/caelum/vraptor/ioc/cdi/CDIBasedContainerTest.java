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
package br.com.caelum.vraptor.ioc.cdi;

import br.com.caelum.cdi.component.UsingCacheComponent;
import br.com.caelum.vraptor.converter.*;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.events.VRaptorInitialized;
import br.com.caelum.vraptor.http.route.*;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.fixture.*;
import br.com.caelum.vraptor.serialization.*;
import org.hamcrest.Matcher;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;
import java.util.Map.Entry;

import static br.com.caelum.vraptor.VRaptorMatchers.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class CDIBasedContainerTest {

	@Inject private CDIBasedContainer cdiBasedContainer;
	@Inject private Event<VRaptorInitialized> initEvent;

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap
			.create(WebArchive.class)
				.addClass(CDIBasedContainer.class)
				.addPackages(true, "br.com.caelum.vraptor")
				.addPackages(true, "br.com.caelum.cdi")
			.addAsManifestResource(INSTANCE, "beans.xml");
	}

	@Test
	public void shouldCreateComponentsWithCache(){
		UsingCacheComponent component = cdiBasedContainer.instanceFor(UsingCacheComponent.class);
		component.putWithLRU("test","test");
		component.putWithDefault("test2","test2");
		assertEquals(component.putWithLRU("test","test"),"test");
		assertEquals(component.putWithDefault("test2","test2"),"test2");
	}

	@Test
	public void shoudRegisterResourcesInRouter() {
		initEvent.fire(new VRaptorInitialized(null));
		Router router = instanceFor(Router.class);
		Matcher<Iterable<? super Route>> hasItem = hasItem(canHandle(ControllerInTheClasspath.class,
				ControllerInTheClasspath.class.getDeclaredMethods()[0]));
		assertThat(router.allRoutes(), hasItem);
	}

	@Test
	public void shoudRegisterConvertersInConverters() {
		Converters converters = instanceFor(Converters.class);
		Converter<?> converter = converters.to(Void.class);
		assertThat(converter, is(instanceOf(ConverterInTheClasspath.class)));
	}

	/**
	 * Check if exist {@link Deserializer} registered in VRaptor for determined Content-Types.
	 */
	@Test
	public void shouldReturnAllDefaultDeserializers() {

		Deserializers deserializers = instanceFor(Deserializers.class);
		List<String> types = asList("application/json", "json", "application/xml",
			"xml", "text/xml", "application/x-www-form-urlencoded");

		for (String type : types) {
			assertThat("deserializer not found: " + type,
					deserializers.deserializerFor(type, cdiBasedContainer), is(notNullValue()));
		}
	}

	@Test
	public void shouldReturnAllDefaultConverters() {
		Converters converters = instanceFor(Converters.class);
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
	}

	@Test
	public void shoudRegisterInterceptorsInInterceptorRegistry() {
		InterceptorRegistry registry = instanceFor(InterceptorRegistry.class);
		assertThat(registry.all(), hasOneCopyOf(InterceptorInTheClasspath.class));
	}

	private <T> T instanceFor(final Class<T> component) {
		return cdiBasedContainer.instanceFor(component);
	}

}
