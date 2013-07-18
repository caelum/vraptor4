package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4x.Accepts;

@Intercepts
public class NonBooleanAcceptsInterceptor{

	@Accepts
	public String accepts(ControllerMethod controllerMethod){
		return "";
	}
}
