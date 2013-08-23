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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(ValidatorCreator.class);

	private ValidatorFactory factory;

	private Validator validator;
	
	//CDI eyes only
	@Deprecated
	public ValidatorCreator() {
	}

	@Inject
    public ValidatorCreator(ValidatorFactory factory) {
        this.factory = factory;
    }
	
    @PostConstruct
    public void createValidator() {
    	validator = factory.getValidator();
    	logger.debug("Initializing Bean Validator");
    }

	
	@Produces @Default @javax.enterprise.context.ApplicationScoped
	public Validator getInstance() {
		return validator;
	}

}