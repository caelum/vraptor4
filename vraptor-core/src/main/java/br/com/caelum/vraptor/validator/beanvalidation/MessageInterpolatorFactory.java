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
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for {@link MessageInterpolator}.
 * 
 * @author Lucas Cavalcanti
 * @since 3.1.3
 *
 */
@ApplicationScoped
public class MessageInterpolatorFactory{

	private static final Logger logger = LoggerFactory.getLogger(MessageInterpolatorFactory.class);

	private final ValidatorFactory factory;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected MessageInterpolatorFactory() {
		this(null);
	}

	@Inject
	public MessageInterpolatorFactory(ValidatorFactory factory) {
		this.factory = factory;
	}
	
	@Produces
	@ApplicationScoped
	public MessageInterpolator getInstance() {
		logger.debug("Initializing Bean Validator MessageInterpolator");
		return factory.getMessageInterpolator();
	}
}
