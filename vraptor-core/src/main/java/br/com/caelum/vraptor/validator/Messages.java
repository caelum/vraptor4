package br.com.caelum.vraptor.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

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
			messages.put(severity, new MessageList(new ArrayList<Message>()));
		}
		return messages.get(severity);
	}

	public List<Message> getErrors() {
		return get(Severity.ERROR);
	}

	public List<Message> getInfo() {
		return get(Severity.INFO);
	}

	public List<Message> getWarn() {
		return get(Severity.WARN);
	}
}
