package br.com.caelum.vraptor.interceptor;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor.controller.ControllerMethod;

@RequestScoped
public class ControllerMethodFactory {
	
	private ControllerMethod method;
	
	
	public void configure(@Observes ControllerMethod method){
		this.method = method;
	}
	
	
	@Produces
	public ControllerMethod getMethod() {
		return method;
	}
	

}
