package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor4.restfulie.controller.ControllerInstance;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;

public interface AcceptsValidator<A> {

	public boolean validate(ControllerMethod method,ControllerInstance instance);
	
	public void initialize(A annotation);
}
