package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

public interface AcceptsValidator<A> {

	public boolean validate(ControllerMethod method,ControllerInstance instance);
	
	public void initialize(A annotation);
}
