package br.com.caelum.vraptor.controller;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;

/**
 * Default 400 handler
 * 
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class DefaultInvalidInputHandler implements InvalidInputHandler {

	private final Result result;
	
	/**
	 * @deprecated CDI eyes only
	 */
	protected DefaultInvalidInputHandler() {
		this(null);
	}

	@Inject
	public DefaultInvalidInputHandler(Result result) {
		this.result = result;
	}

	@Override
	public void deny(InvalidInputException e) {
		result.use(Results.http()).sendError(SC_BAD_REQUEST, e.getMessage());		
	}
}
