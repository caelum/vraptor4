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

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.validator.beanvalidation.BeanValidatorContext;
import br.com.caelum.vraptor.view.ValidationViewsFactory;

/**
 * The default validator implementation.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class DefaultValidator extends AbstractValidator {

	private static final Logger logger = LoggerFactory.getLogger(DefaultValidator.class);

	private final Result result;
	private final ValidationViewsFactory viewsFactory;
	private final Outjector outjector;
	private final Proxifier proxifier;
	private final ResourceBundle bundle;

	private final javax.validation.Validator bvalidator;
	private final MessageInterpolator interpolator;
	private final Locale locale;
	private final Messages messages;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultValidator() {
		this(null, null, null, null, null, null, null, null, null);
	}

	@Inject
	public DefaultValidator(Result result, ValidationViewsFactory factory, Outjector outjector, Proxifier proxifier, 
			ResourceBundle bundle, javax.validation.Validator bvalidator, MessageInterpolator interpolator, Locale locale,
			Messages messages) {
		this.result = result;
		this.viewsFactory = factory;
		this.outjector = outjector;
		this.proxifier = proxifier;
		this.bundle = bundle;
		this.bvalidator = bvalidator;
		this.interpolator = interpolator;
		this.locale = locale;
		this.messages = messages;
	}

	@Override
	public Validator check(boolean condition, Message message) {
		return ensure(condition, message);
	}
	
	@Override
	public Validator ensure(boolean expression, Message message) {
		return addIf(!expression, message);
	}
	
	@Override
	public Validator addIf(boolean expression, Message message) {
		message.setBundle(bundle);
		if (expression) {
			messages.add(message);
		}
		return this;
	}

	@Override
	public Validator validate(Object object, Class<?>... groups) {
		return validate((String) null, object, groups);
	}

	@Override
	public Validator validate(String alias, Object object, Class<?>... groups) {
		if (object != null) {
			addAll(alias, bvalidator.validate(object, groups));
		}
		return this;
	}

	@Override
	public Validator add(Message message) {
		message.setBundle(bundle);
		messages.add(message);
		return this;
	}

	@Override
	public Validator addAll(Collection<? extends Message> messages) {
		for (Message message : messages) {
			add(message);
		}
		return this;
	}

	@Override
	public <T> Validator addAll(Set<ConstraintViolation<T>>  errors) {
		return addAll((String) null, errors);
	}

	@Override
	public <T> Validator addAll(String alias, Set<ConstraintViolation<T>> errors) {
		for (ConstraintViolation<T> v : errors) {
			String msg = interpolator.interpolate(v.getMessageTemplate(), new BeanValidatorContext(v), locale);
			String category = v.getPropertyPath().toString();
			if (!isNullOrEmpty(alias)) {
				category = alias + "." + category;
			}

			add(new SimpleMessage(category, msg));
			logger.debug("added message {}={} for contraint violation", category, msg);
		}
		return this;
	}

	@Override
	public <T extends View> T onErrorUse(Class<T> view) {
		if (!hasErrors()) {
			return new MockResult(proxifier).use(view); //ignore anything, no errors occurred
		}

		result.include("errors", getErrors());
		outjector.outjectRequestMap();
		
		logger.debug("there are errors on result: {}", getErrors());
		return viewsFactory.instanceFor(view, messages.handleErrors());
	}

	@Override
	public boolean hasErrors() {
		return !getErrors().isEmpty();
	}

	@Override
	public List<Message> getErrors() {
		return messages.getErrors();
	}
}
