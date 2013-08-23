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

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.Converter;
import br.com.caelum.vraptor4.VRaptorException;
import br.com.caelum.vraptor4.controller.BeanClass;
import br.com.caelum.vraptor4.core.BaseComponents;
import br.com.caelum.vraptor4.core.ConvertQualifier;
import br.com.caelum.vraptor4.core.Converters;

@ApplicationScoped
public class ConverterHandler{

	private static final Logger logger = LoggerFactory.getLogger(ConverterHandler.class);

	private Converters converters;

	//CDI eyes only
	@Deprecated
	public ConverterHandler() {
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
		if (BaseComponents.getBundledConverters().contains(originalType)) {
			logger.debug("Ignoring handling default converter {}", originalType);
			return;
		}
		@SuppressWarnings("unchecked")
		Class<? extends Converter<?>> converterType = (Class<? extends Converter<?>>) originalType;

		converters.register(converterType);
	}
}
