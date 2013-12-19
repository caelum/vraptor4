package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.observer.RequestHandlerObserver;

/**
 * Event fired by {@link RequestHandlerObserver} just 
 * after start {@link InterceptorStack} execution
 *
 * @author Rodrigo Turini
 * @author Victor Kendy Harada
 */
public class StackStarting {

	private ControllerMethod method;

	public StackStarting(ControllerMethod method) {
		this.method = method;
	}

	public ControllerMethod getControllerMethod() {
		return method;
	}
}