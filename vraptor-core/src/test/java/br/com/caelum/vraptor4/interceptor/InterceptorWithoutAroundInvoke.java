package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.controller.ControllerMethod;

@Intercepts
public class InterceptorWithoutAroundInvoke {

	public void intercept(InterceptorStack stack, ControllerMethod method, Object resourceInstance){
		
	}
}
