package br.com.caelum.vraptor4x.interceptor;

import br.com.caelum.vraptor4x.controller.ControllerInstance;
import br.com.caelum.vraptor4x.controller.ControllerMethod;

public interface AcceptsValidator<A> {

	public boolean validate(ControllerMethod method,ControllerInstance instance);
	
	public void initialize(A annotation);
}
