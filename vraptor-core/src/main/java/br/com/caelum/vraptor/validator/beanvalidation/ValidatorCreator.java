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
package br.com.caelum.vraptor.validator.beanvalidation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Produces an instance for {@link Validator}. To use this class, you need to register 
 * then in your beans.xml as an alternative.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
@ApplicationScoped
@Alternative
public class ValidatorCreator {

	private final ValidatorFactory factory;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected ValidatorCreator() {
		this(null);
	}

	@Inject
	public ValidatorCreator(ValidatorFactory factory) {
		this.factory = factory;
	}

	@Produces
	@ApplicationScoped
	public Validator getInstanceValidator() {
		return factory.getValidator();
	}
}