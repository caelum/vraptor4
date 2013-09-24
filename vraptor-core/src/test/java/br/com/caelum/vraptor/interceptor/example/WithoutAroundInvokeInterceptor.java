package br.com.caelum.vraptor.interceptor.example;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;

@Intercepts
@Vetoed
public class WithoutAroundInvokeInterceptor {

	public void intercept(InterceptorStack stack, ControllerMethod method, ControllerInstance controllerInstance){
		
	}
}
