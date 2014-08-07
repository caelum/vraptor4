package br.com.caelum.vraptor.validator;

import java.util.List;

public interface ValidationErrors {

	boolean hasUnhandledErrors();
	List<Message> handleErrors();
	List<Message> getErrors();
	void add(Message message);
	
}
