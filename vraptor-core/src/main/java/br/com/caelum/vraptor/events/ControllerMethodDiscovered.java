package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.observer.RequestHandlerObserver;

/**
 * fired by {@link RequestHandlerObserver}
 *
 * @author Rodrigo Turini
 */
public class ControllerMethodDiscovered {

	private final ControllerMethod controllerMethod;

	public ControllerMethodDiscovered(ControllerMethod method) {
		this.controllerMethod = method;
	}

	public ControllerMethod getControllerMethod() {
		return controllerMethod;
	}

	public BeanClass getController() {
		return controllerMethod.getController();
	}
}