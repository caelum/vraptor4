package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.core.InterceptorStack;

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
