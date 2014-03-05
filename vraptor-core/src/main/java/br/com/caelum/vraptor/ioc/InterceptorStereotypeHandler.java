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

import static org.slf4j.LoggerFactory.getLogger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.core.InterceptsQualifier;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.InterceptorValidator;

/**
 * Called when a interceptor is discovered, registers it.
 */
@Dependent
public class InterceptorStereotypeHandler {

	private static final Logger logger = getLogger(InterceptorStereotypeHandler.class);

	private final InterceptorRegistry registry;
	private final InterceptorValidator interceptorValidator;

	/**
	 * @deprecated CDI eyes only
	 */
	protected InterceptorStereotypeHandler() {
		this(null, null);
	}

	@Inject
	public InterceptorStereotypeHandler(InterceptorRegistry registry, InterceptorValidator validator) {
		this.registry = registry;
		this.interceptorValidator = validator;
	}

	public void handle(@Observes @InterceptsQualifier BeanClass beanClass) {
		Class<?> originalType = beanClass.getType();
		interceptorValidator.validate(originalType);
		logger.debug("Found interceptor for {}", originalType);
		registry.register(originalType);
	}
}
