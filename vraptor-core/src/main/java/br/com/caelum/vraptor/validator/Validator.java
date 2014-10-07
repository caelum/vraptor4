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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import br.com.caelum.vraptor.View;

/**
 * A validator interface for VRaptor. It allows you to assert for specific situations.
 *
 * @author Guilherme Silveira
 */
public interface Validator {

	/**
	 * Ensure that expression is true, otherwise adds the message to validation errors.
	 * 
	 * @since 4.0.0
	 * @param expression expression to test
	 * @param message the message to add if expression is false.
	 * @return the same validator
	 */
	Validator ensure(boolean expression, Message message);

	/**
	 * alias to ensure
	 * @since 4.0.0
	 * @param expression expression to test
	 * @param message  the message to add if expression is false.
	 * @return the same validator
	 */
	Validator check(boolean expression, Message message);

	/**
	 * Adds the message to validation errors if the expression is true.
	 * 
	 * @since 4.0.0
	 * @param expression expression to test
	 * @param message the message to add if expression is true.
	 * @return the same validator
	 */
	Validator addIf(boolean expression, Message message);

	/**
	 * Validate an object instance using bean validation.
	 * 
	 * @param object object to validate.
	 * @param groups if you want to validate a group of properties.
	 * @return
	 */
	Validator validate(Object object, Class<?>... groups);

	/**
	 * Validate an object instance using bean validation. If the bean have constraint violations, the category
	 * name will appended with alias value.
	 *
	 * @param alias value to prepend in category
	 * @param object object to validate.
	 * @param groups if you want to validate a group of properties.
	 * @return
	 */
	Validator validate(String alias, Object object, Class<?>... groups);

	/**
	 * Adds the message and return the same validator instance.
	 * @param message
	 */
	Validator add(Message message);

	/**
	 * Adds the list of messages and return the same validator instance.
	 * @param messages
	 */
	Validator addAll(Collection<? extends Message> messages);

	/**
	 * Add messages from bean validation translating to vraptor {@link Message}.
	 * @param errors
	 */
	<T> Validator addAll(Set<ConstraintViolation<T>> errors);

	/**
	 * Add messages from bean validation translating to vraptor {@link Message}. When translating the
	 * {@code ConstraintViolation} to {@code Message}, the category name will appended with alias, if the
	 * alias is not null.
	 * 
	 * @param alias value to prepend in category
	 * @param errors
	 */
	<T> Validator addAll(String alias, Set<ConstraintViolation<T>> errors);

	/**
	 * Returns a list of errors.
	 * @return
	 */
	List<Message> getErrors();

	/**
	 * Return true if has validation errors. False otherwise.
	 * @return
	 */
	boolean hasErrors();

	/**
	 * If validator has errors, you can use this method to define what to do. WARN: this method don't stop the flow.
	 * @param view
	 * @return
	 */
	<T extends View> T onErrorUse(Class<T> view);

	/**
	 * Shortcut for <br>
	 * <pre>onErrorUse(logic()).forwardTo(controller);</pre>
	 */
	<T> T onErrorForwardTo(Class<T> controller);
	/**
	 * Shortcut for <br>
	 * <pre>onErrorUse(logic()).forwardTo(controller.getClass());</pre>
	 *
	 * For usage in the same controller:<br>
	 * <pre>validator.onErrorForwardTo(this).someLogic();</pre>
	 */
	<T> T onErrorForwardTo(T controller);

	/**
	 * Shortcut for <br>
	 * <pre>onErrorUse(logic()).redirectTo(controller);</pre>
	 */
	<T> T onErrorRedirectTo(Class<T> controller);
	/**
	 * Shortcut for <br>
	 * <pre>onErrorUse(logic()).redirectTo(controller.getClass());</pre>
	 *
	 * For usage in the same controller:<br>
	 * <pre>validator.onErrorRedirectTo(this).someLogic();</pre>
	 */
	<T> T onErrorRedirectTo(T controller);

	/**
	 * Shortcut for <br>
	 * <pre>onErrorUse(page()).of(controller);</pre>
	 */
	<T> T onErrorUsePageOf(Class<T> controller);
	/**
	 * Shortcut for <br>
	 * <pre>onErrorUse(page()).of(controller.getClass());</pre>
	 *
	 * For usage in the same controller:<br>
	 * <pre>validator.onErrorUsePageOf(this).someLogic();</pre>
	 */
	<T> T onErrorUsePageOf(T controller);

	/**
	 * Shortcut for <br>
	 * <pre>onErrorUse(status()).badRequest(errors);</pre>
	 *
	 * the actual validation errors list will be used.
	 */
	void onErrorSendBadRequest();

}
