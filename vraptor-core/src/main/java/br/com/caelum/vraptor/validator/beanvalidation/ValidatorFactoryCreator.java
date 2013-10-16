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
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produces an instance for {@link ValidatorFactory}.To use this class, you need to register 
 * then in your beans.xml as an alternative.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.5.1
 */
@ApplicationScoped
@Alternative
public class ValidatorFactoryCreator {
	
	private static final Logger logger = LoggerFactory.getLogger(ValidatorFactoryCreator.class);
	
	@Produces
	@ApplicationScoped
	public ValidatorFactory getInstance() {
		logger.debug("Initializing Bean Validator");
		return Validation.byDefaultProvider().configure().buildValidatorFactory();
	}

	public void close(@Disposes ValidatorFactory validatorFactory) {
		validatorFactory.close();
	}
}
