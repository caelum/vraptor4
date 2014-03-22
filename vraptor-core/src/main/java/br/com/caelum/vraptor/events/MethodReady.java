package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.observer.ExecuteMethod;

/**
 * Event fired by {@link ExecuteMethod} just before its execution.
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
public class MethodReady {

	private final ControllerMethod controllermethod;

	public MethodReady(ControllerMethod method) {
		this.controllermethod = method;
	}

	public ControllerMethod getControllerMethod() {
		return controllermethod;
	}
}