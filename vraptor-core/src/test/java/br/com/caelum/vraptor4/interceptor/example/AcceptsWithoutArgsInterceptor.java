package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.restfulie.controller.ControllerInstance;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4x.Accepts;
import br.com.caelum.vraptor4x.AfterCall;
import br.com.caelum.vraptor4x.AroundCall;
import br.com.caelum.vraptor4x.BeforeCall;

@Intercepts
public class AcceptsWithoutArgsInterceptor{

	@Accepts
	public boolean accepts(){
		return true;
	}
	
	@AroundCall
	public void around(InterceptorStack stack, ControllerMethod method, ControllerInstance resourceInstance){
		
	}	
	
	@BeforeCall
	public void before(){
		
	}
	
	@AfterCall
	public void after(){
		
	}	
}
