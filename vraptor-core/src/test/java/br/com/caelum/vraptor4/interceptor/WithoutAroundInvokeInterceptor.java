package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

@Intercepts
public class WithoutAroundInvokeInterceptor {

	public void intercept(InterceptorStack stack, ControllerMethod method, ControllerInstance resourceInstance){
		
	}
}
