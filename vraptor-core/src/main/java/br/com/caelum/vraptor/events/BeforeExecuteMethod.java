package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.controller.ControllerMethod;

/**
 * Fired just before {@link ReadyToExecuteMethod} event.
 *
 * @author Rodrigo Turini
 */
public class BeforeExecuteMethod {

	private ControllerMethod controllerMethod;

	public BeforeExecuteMethod(ControllerMethod controllerMethod) {
		this.controllerMethod = controllerMethod;
	}

	public ControllerMethod getControllerMethod() {
		return controllerMethod;
	}
}
