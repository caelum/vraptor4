package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

@Intercepts
public class AlwaysAcceptsAspectInterceptor{
	
	@BeforeCall
	public void begin(){
		
	}

	@AroundCall
	public void intercept(InterceptorStack stack, ControllerMethod method, ControllerInstance controllerInstance){
	}
		

	@AfterCall
	public void after() {
		
	}
}
