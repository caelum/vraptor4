package br.com.caelum.vraptor4.interceptor;

import javax.interceptor.AroundInvoke;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.AfterInvoke;
import br.com.caelum.vraptor4.BeforeInvoke;
import br.com.caelum.vraptor4.controller.ControllerMethod;

@Intercepts
public class AcceptsInterceptor {

	private boolean accepts;

	public AcceptsInterceptor(boolean accepts) {
		this.accepts = accepts;
	}
	
	@BeforeInvoke
	public void before(){
		
	}
	
	@AfterInvoke
	public void after(){
		
	}

	@Accepts
	public boolean accepts(){
		return this.accepts;
	}
	
	@AroundInvoke
	public void around(InterceptorStack stack, ControllerMethod method, Object resourceInstance){
		
	}
}
