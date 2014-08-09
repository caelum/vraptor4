package br.com.caelum.vraptor.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

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
@Named
@RequestScoped
public class Messages {

	private Map<Severity, List<Message>> messages = new HashMap<>();

	public Messages add(Message message) {
		get(message.getSeverity()).add(message);
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

	private MessageList createMessageList() {
		return new MessageList(new ArrayList<Message>());
	}
}
