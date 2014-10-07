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

package br.com.caelum.vraptor.util.test;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.enterprise.inject.Vetoed;
import javax.validation.ConstraintViolation;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.validator.AbstractValidator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.validator.Validator;

/**
 * Mocked Validator for testing your controllers.
 *
 * You can use the idiom:
 * MockValidator validator = new MockValidator();
 * MyController controller = new MyController(..., validator);
 *
 * try {
 * 		controller.method();
 * 		Assert.fail();
 * } catch (ValidationError e) {
 * 		List&lt;Message&gt; errors = e.getErrors();
 * 		// asserts
 * }
 *
 * or
 *
 * \@Test(expected=ValidationError.class)
 *
 * @author Lucas Cavalcanti
 */
@Vetoed
public class MockValidator extends AbstractValidator {

	private List<Message> errors = new ArrayList<>();

	@Override
	public Validator check(boolean condition, Message message) {
		return ensure(condition, message);
	}
	
	@Override
	public Validator addIf(boolean expression, Message message) {
		if (expression) {
			add(message);
		}
		return this;
	}
	@Override
	public Validator ensure(boolean expression, Message message) {
		return addIf(!expression, message);
	}

	@Override
	public Validator validate(Object object, Class<?>... groups) {
		return this;
	}

	@Override
	public Validator validate(String alias, Object object, Class<?>... groups) {
		return this;
	}

	@Override
	public <T> Validator addAll(String alias, Set<ConstraintViolation<T>> errors) {
		for (ConstraintViolation<T> v : errors) {
			String category = v.getPropertyPath().toString();
			if (isNullOrEmpty(alias)) {
				category = alias + "." + category;
			}

			add(new SimpleMessage(category, v.getMessage()));
		}
		return this;
	}

	@Override
	public <T> Validator addAll(Set<ConstraintViolation<T>> errors) {
		return addAll((String) null, errors);
	}

	@Override
	public <T extends View> T onErrorUse(Class<T> view) {
		if(!this.errors.isEmpty()) {
			throw new ValidationException(errors);
		}
		return new MockResult().use(view);
	}

	@Override
	public Validator addAll(Collection<? extends Message> messages) {
		for(Message message: messages) {
			add(message);
		}
		return this;
	}

	@Override
	public Validator add(Message message) {
		errors.add(message);
		return this;
	}

	@Override
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	@Override
	public List<Message> getErrors() {
		return errors;
	}
	
	public boolean containsMessage(String messageKey, Object... messageParameters) {
		I18nMessage expectedMessage = new I18nMessage("validation", messageKey, messageParameters);
		expectedMessage.setBundle(ResourceBundle.getBundle("messages"));
		for(Message error : this.getErrors()) {
			if(expectedMessage.getMessage().equals(error.getMessage())) {
				return true;
			}
		}

		return false;
	}
}
