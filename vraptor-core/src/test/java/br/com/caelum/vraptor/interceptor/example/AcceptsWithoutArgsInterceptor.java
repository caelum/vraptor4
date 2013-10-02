package br.com.caelum.vraptor.interceptor.example;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;

@Intercepts
@Vetoed
public class AcceptsWithoutArgsInterceptor{

	@Accepts
	public boolean accepts(){
		return true;
	}
	
	@AroundCall
	public void around(InterceptorStack stack, ControllerMethod method, ControllerInstance controllerInstance){
		
	}	
	
	@BeforeCall
	public void before(){
		
	}
	
	@AfterCall
	public void after(){
		
	}	
}
