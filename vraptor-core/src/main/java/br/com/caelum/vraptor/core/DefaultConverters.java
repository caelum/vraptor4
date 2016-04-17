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
import java.util.List;

import javax.annotation.Priority;
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

import com.google.common.base.Supplier;

/**
 * Default implementation for {@link Converters}.
 *
 * @author Guilherme Silveira
 * @author Rodrigo Turini
 * @author Lucas Cavalcanti
 * @author Otávio Scherer Garcia
 */
@ApplicationScoped
public class DefaultConverters implements Converters {

	private final Logger logger = LoggerFactory.getLogger(DefaultConverters.class);
	private final List<Class<? extends Converter<?>>> classes = new LinkedList<>();

	private final CacheStore<Class<?>, Class<? extends Converter<?>>> cache;
	private final Container container;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultConverters() {
		this(null, null);
	}

	@Inject
	public DefaultConverters(Container container, @LRU CacheStore<Class<?>, Class<? extends Converter<?>>> cache) {
		this.container = container;
		this.cache = cache;
		logger.info("Registering bundled converters");
	}

	@Override
	public void register(Class<? extends Converter<?>> converterClass) {
		Convert type = converterClass.getAnnotation(Convert.class);
		checkState(type != null, "The converter type %s should have the Convert annotation", converterClass.getName());

		Class<? extends Converter<?>> currentConverter = findConverterType(type.value());
		if (!currentConverter.equals(NullConverter.class)) {
			int priority = getConverterPriority(converterClass);
			int priorityCurrent = getConverterPriority(currentConverter);

			Convert currentType = currentConverter.getAnnotation(Convert.class);
			checkState(priority != priorityCurrent || !type.value().equals(currentType.value()),
					"Converter %s have same priority than %s", converterClass, currentConverter);

			if (priority > priorityCurrent) {
				logger.debug("Overriding converter {} with {} because have more priority", currentConverter, converterClass);
				classes.remove(currentConverter);
				classes.add(converterClass);
			} else {
				logger.debug("Converter {} not registered because have less priority than {}", converterClass, currentConverter);
			}
		}

		logger.debug("adding converter {} to {}", converterClass, type.value());
		classes.add(converterClass);
	}

	private int getConverterPriority(Class<? extends Converter<?>> converter) {
		Priority priority = converter.getAnnotation(Priority.class);
		return priority == null ? 0 : priority.value();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Converter<T> to(Class<T> clazz) {
		Class<? extends Converter<?>> converterType = findConverterTypeFromCache(clazz);
		checkState(!converterType.equals(NullConverter.class), "Unable to find converter for %s", clazz.getName());

		logger.debug("found converter {} to {}", converterType.getName(), clazz.getName());
		return (Converter<T>) container.instanceFor(converterType);
	}

	private Class<? extends Converter<?>> findConverterTypeFromCache(final Class<?> clazz) {
		return cache.fetch(clazz, new Supplier<Class<? extends Converter<?>>>() {

			@Override
			public Class<? extends Converter<?>> get() {
				return findConverterType(clazz);
			}

		});
	}

	private Class<? extends Converter<?>> findConverterType(final Class<?> clazz) {
		Class<? extends Converter<?>> found = null;
		Class<?> foundType = null;
		for (Class<? extends Converter<?>> current : classes) {
			Class<?> boundType = current.getAnnotation(Convert.class).value();
			if (boundType.equals(clazz)) {
				return current;
			}
			if (boundType.isAssignableFrom(clazz)) {
				if (foundType == null || foundType.isAssignableFrom(boundType)) {
					foundType = boundType;
					found = current;
				}
			}
		}
		if (found != null) {
			return found;
		}

		logger.debug("Unable to find a converter for {}. Returning NullConverter.", clazz);
		return NullConverter.class;
	}

	private interface NullConverter extends Converter<Object> {};

	@Override
	public boolean existsFor(Class<?> type) {
		return !findConverterTypeFromCache(type).equals(NullConverter.class);
	}

	@Override
	public boolean existsTwoWayFor(Class<?> type) {
		return TwoWayConverter.class.isAssignableFrom(findConverterTypeFromCache(type));
	}

	@Override
	public TwoWayConverter<?> twoWayConverterFor(Class<?> type) {
		checkState(existsTwoWayFor(type), "Unable to find two way converter for %s", type.getName());

		return (TwoWayConverter<?>) container.instanceFor(findConverterTypeFromCache(type));
	}
}
