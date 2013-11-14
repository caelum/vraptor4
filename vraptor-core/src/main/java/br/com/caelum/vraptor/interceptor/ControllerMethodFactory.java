package br.com.caelum.vraptor.interceptor;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.events.ControllerMethodDiscovered;

@RequestScoped
public class ControllerMethodFactory {

	private ControllerMethod method;

	public void configure(@Observes ControllerMethodDiscovered event){
		this.method = event.getControllerMethod();
	}

	@Produces
	public ControllerMethod getMethod() {
		return method;
	}
}