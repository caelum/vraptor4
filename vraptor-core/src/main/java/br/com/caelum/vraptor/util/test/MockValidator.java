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

import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.enterprise.inject.Alternative;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.validator.AbstractValidator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
 * 		List<Message> errors = e.getErrors();
 * 		// asserts
 * }
 *
 * or
 *
 * @Test(expected=ValidationError.class)
 *
 * @author Lucas Cavalcanti
 */
@Alternative
public class MockValidator extends AbstractValidator {

	private final List<Message> errors = new ArrayList<>();
	
	@Override
	public Validator check(boolean condition, Message message) {
		return this;
	}

	@Override
	public void validate(Object object, Class<?>... groups) {
	}

	@Override
	public void validateProperties(Object object, String... properties) {
	}
	
	@Override
	public void validateProperty(Object object, String property, Class<?>... groups) {
	}

	@Override
	public <T extends View> T onErrorUse(Class<T> view) {
		if(!this.errors.isEmpty()) {
			throw new ValidationException(errors);
		}
		return new MockResult().use(view);
	}

	@Override
	public void addAll(Collection<? extends Message> messages) {
		this.errors.addAll(messages);
	}

	@Override
	public void add(Message message) {
		this.errors.add(message);
	}

	@Override
	public boolean hasErrors() {
		return !this.errors.isEmpty();
	}

	@Override
	public List<Message> getErrors() {
		return Collections.unmodifiableList(errors);
	}
	
	@Override
	public Map<String, Collection<String>> getErrorsAsMap() {
		if (hasErrors()) {
			Multimap<String, String> messages = ArrayListMultimap.create();
			for (Message m : errors) {
				messages.put(m.getCategory(), m.getMessage());
			}
			
			return messages.asMap();
		}
		
		return emptyMap();
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
