package br.com.caelum.vraptor.events;

import java.lang.reflect.Type;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;

/**
 * Event fired by {@link ExecuteMethodInterceptor}
 * when it has fully completed it's execution.
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
public class MethodExecuted {

	private ControllerMethod controllerMethod;

	public MethodExecuted(ControllerMethod method) {
		this.controllerMethod = method;
	}

	public ControllerMethod getControllerMethod() {
		return controllerMethod;
	}

	public Type getMethodReturnType() {
		return getControllerMethod().getMethod().getGenericReturnType();
	}
}