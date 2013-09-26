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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.TwoWayConverter;
import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
public class DefaultConverters implements Converters {

	private  final Logger logger = LoggerFactory.getLogger(DefaultConverters.class);
	private final Map<Class<?>, Class<? extends Converter<?>>> classes = new LinkedHashMap<>();
	private Container container;

	@Deprecated //CDI eyes only
	public DefaultConverters() {}

	@Inject
	public DefaultConverters(Container container) {
		this.container = container;
		logger.info("Registering bundled converters");
	}

	@Override
	public void register(Class<? extends Converter<?>> converterClass) {
		Convert type = converterClass.getAnnotation(Convert.class);
		checkNotNull(type, "The converter type %s should have the Convert annotation", converterClass.getName());
		
		logger.debug("adding converter {} to {}", converterClass, type.value());
		classes.put(type.value(), converterClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Converter<T> to(Class<T> clazz) {
		Class<? extends Converter<?>> converterType = findConverterType(clazz);
		checkState(converterType != null, "Unable to find converter for %s", clazz.getName());
		
		return (Converter<T>) container.instanceFor(converterType);
	}

	private Class<? extends Converter<?>> findConverterType(Class<?> clazz) {
		return classes.get(clazz);
	}

	@Override
	public boolean existsFor(Class<?> type) {
		return classes.containsKey(type);
	}

	@Override
	public boolean existsTwoWayFor(Class<?> type) {
		Class<? extends Converter<?>> found = findConverterType(type);
		return found != null && TwoWayConverter.class.isAssignableFrom(found);
	}

	@Override
	public TwoWayConverter<?> twoWayConverterFor(Class<?> type) {
		checkState(existsTwoWayFor(type), "Unable to find two way converter for %s", type.getName());
		
		return (TwoWayConverter<?>) container.instanceFor(findConverterType(type));
	}
}
