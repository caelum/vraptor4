package br.com.caelum.vraptor.validator;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class DefaultValidationErrors implements ValidationErrors {

	private final List<Message> errors = new ArrayList<Message>();
	private boolean handled = false;
	
	@Override
	public boolean hasUnhandledErrors() {
		return handled;
	}

	@Override
	public List<Message> handleErrors() {
		handled = true;
		return null;
	}

	@Override
	public List<Message> getErrors() {
		return errors;
	}

	@Override
	public void add(Message message) {
		errors.add(message);
		handled = false;
	}

}
