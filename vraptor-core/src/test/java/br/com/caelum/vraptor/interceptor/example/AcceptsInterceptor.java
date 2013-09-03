package br.com.caelum.vraptor.interceptor.example;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;

@Intercepts
public class AcceptsInterceptor {

	private boolean accepts;

	public AcceptsInterceptor(boolean accepts) {
		this.accepts = accepts;
	}
	
	@BeforeCall
	public void before(){
		
	}
	
	@AfterCall
	public void after(){
		
	}

	@Accepts
	public boolean accepts(ControllerMethod method){
		return this.accepts;
	}
	
	@AroundCall
	public void around(InterceptorStack stack, ControllerMethod method, ControllerInstance controllerInstance){
		
	}
}
