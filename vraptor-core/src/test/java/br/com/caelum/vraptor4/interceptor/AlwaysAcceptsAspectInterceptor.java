package br.com.caelum.vraptor4.interceptor;

import javax.interceptor.AroundInvoke;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.AfterInvoke;
import br.com.caelum.vraptor4.BeforeInvoke;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

@Intercepts
public class AlwaysAcceptsAspectInterceptor{
	
	@BeforeInvoke
	public void begin(){
		
	}

	@AroundInvoke
	public void intercept(InterceptorStack stack, ControllerMethod method, ControllerInstance controllerInstance){
	}
		

	@AfterInvoke
	public void after() {
		
	}
}
