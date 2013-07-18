package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.restfulie.controller.ControllerInstance;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;

@Intercepts
public class WithoutAroundInvokeInterceptor {

	public void intercept(InterceptorStack stack, ControllerMethod method, ControllerInstance resourceInstance){
		
	}
}
