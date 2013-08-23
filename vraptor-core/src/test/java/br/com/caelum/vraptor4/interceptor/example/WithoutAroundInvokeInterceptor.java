package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.core.InterceptorStack;

@Intercepts
public class WithoutAroundInvokeInterceptor {

	public void intercept(InterceptorStack stack, ControllerMethod method, ControllerInstance controllerInstance){
		
	}
}
