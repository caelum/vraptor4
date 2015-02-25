package br.com.caelum.vraptor.validator;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.VRaptorException;

@Vetoed
public class ValidationFailedException extends VRaptorException {

	private static final long serialVersionUID = 3495204717080278982L;

	public ValidationFailedException(String message) {
		super(message);
	}
}
