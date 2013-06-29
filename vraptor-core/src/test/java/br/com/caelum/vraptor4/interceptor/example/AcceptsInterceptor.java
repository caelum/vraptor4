package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

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
	public void around(InterceptorStack stack, ControllerMethod method, ControllerInstance resourceInstance){
		
	}
}
