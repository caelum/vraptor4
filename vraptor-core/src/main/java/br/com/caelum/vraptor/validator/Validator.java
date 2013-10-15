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
	 * @param message {@link Message} object
	 */
	Validator check(boolean expression, Message message);
	
	/**
	 * If validator has errors, you can use this method to define what to do. WARN: this method don't stop the flow.
	 * @param view
	 * @return
	 */
	<T extends View> T onErrorUse(Class<T> view);

	/**
	 * Adds the list of messages.
	 * @param messages
	 */
	void addAll(Collection<? extends Message> messages);

	/**
	 * Adds the message.
	 * @param message
	 */
	void add(Message message);

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
