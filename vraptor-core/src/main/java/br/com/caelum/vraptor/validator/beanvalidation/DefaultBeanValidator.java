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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.validator.BeanValidator;
import br.com.caelum.vraptor.validator.BeanValidatorContext;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationMessage;

/**
 * Implements the {@link BeanValidator} using Bean Validation (JSR303). This implementation
 * will be enable by vraptor when any implementation of Bean Validation is available into classpath.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
@RequestScoped
public class DefaultBeanValidator
    implements BeanValidator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanValidator.class);

    private Localization localization;

    private Validator validator;

    private MessageInterpolator interpolator;

    //CDI eyes only
	@Deprecated
	public DefaultBeanValidator() {
	}
    
    @Inject
    public DefaultBeanValidator(Localization localization, Validator validator, MessageInterpolator interpolator) {
        this.localization = localization;
        this.validator = validator;
        this.interpolator = interpolator;
    }

    @Override
	public List<Message> validate(Object bean, Class<?>... groups) {
        if (bean == null) {
            logger.warn("skiping validation, input bean is null.");
            return emptyList();
        }

        final Set<ConstraintViolation<Object>> violations = validator.validate(bean, groups);
        logger.debug("there are {} violations at bean {}.", violations.size(), bean);

        return getMessages(violations);
    }

    @Override
	public List<Message> validateProperties(Object bean, String... properties) {
    	if(bean == null) {
    		logger.warn("skiping validation, input bean is null.");
            return emptyList();
    	}
    	
    	checkArgument(hasProperties(properties), "No properties were defined to be validated");
    	
    	List<Message> messages = new ArrayList<>();
    	
    	for(String property : properties) {
            Set<ConstraintViolation<Object>> violations = validator.validateProperty(bean, property);
            logger.debug("there are {} violations at bean {}.", violations.size(), bean);

            messages.addAll(getMessages(violations));
        }
    	
    	return messages;
    }
    
	@Override
	public List<Message> validateProperty(Object bean, String property, Class<?>... groups) {
		if (bean == null) {
			logger.warn("skiping validation, input bean is null.");
			return emptyList();
		}

		Set<ConstraintViolation<Object>> violations = validator.validateProperty(bean, property, groups);
		logger.debug("there are {} violations at bean {}.", violations.size(), bean);

		return getMessages(violations);
	}
    
    private List<Message> getMessages(final Set<ConstraintViolation<Object>> violations) {
    	List<Message> messages = new ArrayList<>();
    	
    	for(ConstraintViolation<Object> v : violations) {
    		BeanValidatorContext ctx = new BeanValidatorContext(v);
    		String msg = interpolator.interpolate(v.getMessageTemplate(), ctx, getLocale());
    		messages.add(new ValidationMessage(msg, v.getPropertyPath().toString()));
    		
    		logger.debug("added message {} to validation of bean {}", msg, v.getRootBean());
    	}
    	
    	return messages;
    }

    private Locale getLocale() {
    	return localization.getLocale() == null ? Locale.getDefault() : localization.getLocale();
    }

    private boolean hasProperties(String... properties) {
    	return properties != null && properties.length > 0;
    }
}
