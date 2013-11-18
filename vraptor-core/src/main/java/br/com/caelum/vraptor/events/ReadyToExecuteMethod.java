package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;

/**
 * Event fired by {@link ParametersInstantiatorInterceptor}.
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
public class ReadyToExecuteMethod {

	private final ControllerMethod controllermethod;

	public ReadyToExecuteMethod(ControllerMethod method) {
		this.controllermethod = method;
	}

	public ControllerMethod getControllerMethod() {
		return controllermethod;
	}
}