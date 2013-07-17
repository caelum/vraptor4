package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.controller.ControllerMethod;

@Intercepts
public class NonBooleanAcceptsInterceptor{

	@Accepts
	public String accepts(ControllerMethod controllerMethod){
		return "";
	}
}
