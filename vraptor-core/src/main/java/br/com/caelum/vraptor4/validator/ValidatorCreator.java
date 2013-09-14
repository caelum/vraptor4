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
package br.com.caelum.vraptor4.validator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

/**
 * Bring up Bean Validation factory. This class builds the {@link Validator} once when application
 * starts.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
@ApplicationScoped
@Alternative
public class ValidatorCreator {

	private ValidatorFactory factory;
	
	//CDI eyes only
	@Deprecated
	public ValidatorCreator() {
	}

	@Inject
    public ValidatorCreator(ValidatorFactory factory) {
        this.factory = factory;
    }
	
	@Produces @javax.enterprise.context.RequestScoped
	public Validator getInstanceValidator() {
		return factory.getValidator();
	}
	
	@Produces @javax.enterprise.context.RequestScoped
	public ExecutableValidator getInstanceExecutable() {
		return factory.getValidator().forExecutables();
	}
}