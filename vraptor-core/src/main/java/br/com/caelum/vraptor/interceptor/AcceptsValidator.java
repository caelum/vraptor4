package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

public interface AcceptsValidator<A> {

	boolean validate(ControllerMethod method,ControllerInstance instance);
	
	void initialize(A annotation);
}
