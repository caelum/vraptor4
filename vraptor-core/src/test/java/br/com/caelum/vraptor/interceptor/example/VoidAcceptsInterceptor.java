package br.com.caelum.vraptor.interceptor.example;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;

@Intercepts
public class VoidAcceptsInterceptor {

	@Accepts
	public void accepts(ControllerMethod controllerMethod){		
	}
}
