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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.core.ConvertQualifier;
import br.com.caelum.vraptor.core.Converters;

/**
 * Called when a converter is discovered, registers it.
 */
@Dependent
public class ConverterHandler{

	private final Converters converters;

	/**
	 * @deprecated CDI eyes only
	 */
	protected ConverterHandler() {
		this(null);
	}

	@Inject
	public ConverterHandler(Converters converters) {
		this.converters = converters;
	}

	public void handle(@Observes @ConvertQualifier BeanClass beanClass) {
		Class<?> originalType = beanClass.getType();
		if (!(Converter.class.isAssignableFrom(originalType))) {
			throw new VRaptorException("converter does not implement Converter");
		}
		@SuppressWarnings("unchecked")
		Class<? extends Converter<?>> converterType = (Class<? extends Converter<?>>) originalType;
		converters.register(converterType);
	}
}
