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

package br.com.caelum.vraptor.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.view.ValidationViewsFactory;

import com.google.common.base.Supplier;

/**
 * The default validator implementation.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class DefaultValidator extends AbstractValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultValidator.class);

	private Result result;
	private List<Message> errors = new ArrayList<>();
	private ValidationViewsFactory viewsFactory;
	private BeanValidator beanValidator;
	private Outjector outjector;
	private Proxifier proxifier;
	private Localization localization;
	
	//CDI eyes only
	@Deprecated
	public DefaultValidator() {
	}

	@Inject
	public DefaultValidator(Result result, ValidationViewsFactory factory, Outjector outjector, Proxifier proxifier, BeanValidator beanValidator, Localization localization) {
		this.result = result;
		this.viewsFactory = factory;
		this.outjector = outjector;
		this.proxifier = proxifier;
		this.beanValidator = beanValidator;
		this.localization = localization;
	}
	
	@Override
	public Validator check(boolean condition, Message message) {
		if (!condition) {
			errors.put(message.getCategory(), message);
		}
		
		return this;
	}
	
	@Override
	public void validate(Object object, Class<?>... groups) {
		addAll(beanValidator.validate(object, groups));
	}
	
	@Override
	public void validateProperties(Object object, String... properties) {
		addAll(beanValidator.validateProperties(object, properties));
	}
	
	@Override
	public void validateProperty(Object object, String property, Class<?>... groups) {
		addAll(beanValidator.validateProperty(object, property, groups));
	}

	@Override
	public <T extends View> T onErrorUse(Class<T> view) {
		if (!hasErrors()) {
			return new MockResult(proxifier).use(view); //ignore anything, no errors occurred
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("there are errors on result: {}", errors);
		}
		
		result.include("errors", getErrors());
		outjector.outjectRequestMap();
		
		return viewsFactory.instanceFor(view, errors);
	}

	@Override
	public void addAll(Collection<? extends Message> messages) {
		for (Message message : messages) {
			add(message);
		}
	}

	@Override
	public void add(Message message) {
		if (message instanceof I18nMessage && !((I18nMessage) message).hasBundle()) {
			((I18nMessage) message).setLazyBundle(new LocalizationSupplier(localization));
		}
		errors.add(message);
	}

	@Override
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	@Override
	public List<Message> getErrors() {
		return new ErrorList(errors);
	}
}

class LocalizationSupplier implements Supplier<ResourceBundle> {
	
	private final Localization localization;

	public LocalizationSupplier(Localization localization) {
		this.localization = localization;
	}

	@Override
	public ResourceBundle get() {
		return localization.getBundle();
	}
}

