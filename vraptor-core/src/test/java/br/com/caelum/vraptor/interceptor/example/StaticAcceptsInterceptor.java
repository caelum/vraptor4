package br.com.caelum.vraptor.interceptor.example;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.StaticAccepts;

@Intercepts
@StaticAccepts
public class StaticAcceptsInterceptor {

	private boolean accepts;

	public StaticAcceptsInterceptor(boolean accepts) {
		super();
		this.accepts = accepts;
	}
	
	@Accepts
	public boolean accepts(ControllerMethod controllerMethod){
		return accepts;
	}
	
}
