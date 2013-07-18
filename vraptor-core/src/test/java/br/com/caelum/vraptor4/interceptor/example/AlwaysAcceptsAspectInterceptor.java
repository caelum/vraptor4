package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4x.AfterCall;
import br.com.caelum.vraptor4x.AroundCall;
import br.com.caelum.vraptor4x.BeforeCall;
import br.com.caelum.vraptor4x.controller.ControllerInstance;
import br.com.caelum.vraptor4x.controller.ControllerMethod;

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
