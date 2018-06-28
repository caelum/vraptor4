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
package br.com.caelum.vraptor.serialization;

import static com.google.common.base.Preconditions.checkArgument;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.core.DeserializesQualifier;

/**
 * Handles classes annotated with {@link Deserializes}.
 *
 * @author Lucas Cavalcanti, Cecilia Fernandes
 * @since 3.0.2
 */
@Dependent
public class DeserializesHandler{

	private final Deserializers deserializers;

	/**
	 * @deprecated CDI eyes only
	 */
	protected DeserializesHandler() {
		this(null);
	}

	@Inject
	public DeserializesHandler(Deserializers deserializers) {
		this.deserializers = deserializers;
	}

	@SuppressWarnings("unchecked")
	public void handle(@Observes @DeserializesQualifier BeanClass beanClass) {
		Class<?> originalType = beanClass.getType();
		checkArgument(Deserializer.class.isAssignableFrom(originalType), "%s must implement Deserializer", beanClass);
		deserializers.register((Class<? extends Deserializer>) originalType);
	}
}
