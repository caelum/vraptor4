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
package br.com.caelum.vraptor4.deserialization;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.core.BaseComponents;
import br.com.caelum.vraptor4.core.DeserializesQualifier;
import br.com.caelum.vraptor4.ioc.ApplicationScoped;
import br.com.caelum.vraptor4.restfulie.controller.BeanClass;

/**
 * Handles classes annotated with @Deserializes
 *
 * @author Lucas Cavalcanti, Cecilia Fernandes
 * @since 3.0.2
 */
@ApplicationScoped
public class DeserializesHandler{

	private static final Logger logger = LoggerFactory.getLogger(DeserializesHandler.class);

	private Deserializers deserializers;
	
	//CDI eyes only
	@Deprecated
	public DeserializesHandler() {
	}

	@Inject
	public DeserializesHandler(Deserializers deserializers) {
		this.deserializers = deserializers;
	}
	
	public void handle(@Observes @DeserializesQualifier BeanClass beanClass) {
		Class<?> originalType = beanClass.getType();
		if (!Deserializer.class.isAssignableFrom(originalType)) {
			throw new IllegalArgumentException(beanClass + " must implement Deserializer");
		}
		if (BaseComponents.getDeserializers().contains(originalType)) {
			logger.debug("Ignoring default deserializer {}", originalType);
			return;
		}

		deserializers.register((Class<? extends Deserializer>) originalType);
	}

}
