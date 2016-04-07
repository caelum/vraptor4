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
package br.com.caelum.vraptor.validator;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import org.slf4j.Logger;

/**
 * Managed class that stores all application messages like errors, warnings and info. This
 * class is useful to display messages categorized by severity in your view. To choose a severity
 * you can construct your message like this:
 * 
 * <code>
 * 	Message message = new SimpleMessage("name", "An info message", Severity.INFO);
 * 	validation.add(message); // will stored as INFO severity
 * </code>
 * 
 * @since 4.1
 * @author Otávio S Garcia
 */
@Vetoed
public class Messages implements Serializable {

	private static final long serialVersionUID = 728589528391504120L;

	private final static Logger log = getLogger(Messages.class);
	
	private final Map<Severity, List<Message>> messages = new HashMap<>();
	private boolean unhandledErrors;

	public Messages add(Message message) {
		get(message.getSeverity()).add(message);
		if(Severity.ERROR.equals(message.getSeverity())) {
			unhandledErrors = true;
		}
		return this;
	}

	private List<Message> get(Severity severity) {
		if (!messages.containsKey(severity)) {
			messages.put(severity, createMessageList());
		}
		return messages.get(severity);
	}

	public List<Message> getErrors() {
		return get(Severity.ERROR);
	}

	public List<Message> getInfo() {
		return get(Severity.INFO);
	}

	public List<Message> getWarnings() {
		return get(Severity.WARN);
	}
	
	public List<Message> getSuccess() {
		return get(Severity.SUCCESS);
	}

	public List<Message> getAll() {
		List<Message> allMessages = createMessageList();
		allMessages.addAll(get(Severity.ERROR));
		allMessages.addAll(get(Severity.WARN));
		allMessages.addAll(get(Severity.INFO));
		allMessages.addAll(get(Severity.SUCCESS));

		return new MessageList(allMessages);
	}

	private MessageList createMessageList() {
		return new MessageList(new ArrayList<Message>());
	}
	
	public List<Message> handleErrors() {
		unhandledErrors = false;
		return getErrors();
	}
	
	public boolean hasUnhandledErrors() {
		return unhandledErrors;
	}
	
	public void assertAbsenceOfErrors() {
		if (hasUnhandledErrors()) {
			log.debug("Some validation errors occured: {}", getErrors());
			
			throw new ValidationFailedException(
				"There are validation errors and you forgot to specify where to go. Please add in your method "
				+ "something like:\n"
				+ "validator.onErrorUse(page()).of(AnyController.class).anyMethod();\n"
				+ "or any view that you like.\n"
				+ "If you didn't add any validation error, it is possible that a conversion error had happened.");
		}
	}
	
}
