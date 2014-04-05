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

import static com.google.common.base.Preconditions.checkState;

import java.util.LinkedList;
import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.TwoWayConverter;
import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.LRU;
import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.ioc.Container;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@ApplicationScoped
public class DefaultConverters implements Converters {

	private final Logger logger = LoggerFactory.getLogger(DefaultConverters.class);
	private final LinkedList<Class<? extends Converter<?>>> classes = new LinkedList<>();

	@LRU
	private final CacheStore<Class<?>, Class<? extends Converter<?>>> cache;
	private final Container container;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultConverters() {
		this(null, null);
	}

	@Inject
	public DefaultConverters(Container container, CacheStore<Class<?>, Class<? extends Converter<?>>> cache) {
		this.container = container;
		this.cache = cache;
		logger.info("Registering bundled converters");
	}

	@Override
	public void register(Class<? extends Converter<?>> converterClass) {
		Convert type = converterClass.getAnnotation(Convert.class);
		checkState(type != null, "The converter type %s should have the Convert annotation", converterClass.getName());

		logger.debug("adding converter {} to {}", converterClass, type.value());
		classes.add(converterClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Converter<T> to(Class<T> clazz) {
		Class<? extends Converter<?>> converterType = findConverterType(clazz);
		checkState(!converterType.equals(NullConverter.class), "Unable to find converter for %s", clazz.getName());

		return (Converter<T>) container.instanceFor(converterType);
	}

	private Class<? extends Converter<?>> findConverterType(final Class<?> clazz) {
		return cache.fetch(clazz, new Callable<Class<? extends Converter<?>>>() {
			@Override
			public Class<? extends Converter<?>> call() throws Exception {
				return FluentIterable.from(classes).filter(matchConverter(clazz))
						.first().or(NullConverter.class);
			}
		});
	}

	private Predicate<Class<?>> matchConverter(final Class<?> clazz) {
		return new Predicate<Class<?>>() {
			@Override
			public boolean apply(Class<?> input) {
				Class<?> boundType = input.getAnnotation(Convert.class).value();
				return boundType.isAssignableFrom(clazz);
			}
		};
	}

	private interface NullConverter extends Converter<Object> {};

	@Override
	public boolean existsFor(Class<?> type) {
		return !findConverterType(type).equals(NullConverter.class);
	}

	@Override
	public boolean existsTwoWayFor(Class<?> type) {
		return TwoWayConverter.class.isAssignableFrom(findConverterType(type));
	}

	@Override
	public TwoWayConverter<?> twoWayConverterFor(Class<?> type) {
		checkState(existsTwoWayFor(type), "Unable to find two way converter for %s", type.getName());

		return (TwoWayConverter<?>) container.instanceFor(findConverterType(type));
	}
}
