package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.restfulie.controller.ControllerInstance;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4x.AfterCall;
import br.com.caelum.vraptor4x.AroundCall;
import br.com.caelum.vraptor4x.BeforeCall;

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
