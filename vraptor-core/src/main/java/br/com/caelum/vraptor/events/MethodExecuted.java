package br.com.caelum.vraptor.events;

import java.lang.reflect.Type;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.observer.ExecuteMethod;

/**
 * Event fired by {@link ExecuteMethod}
 * when it has fully completed it's execution.
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
public class MethodExecuted {

	private final ControllerMethod controllerMethod;
	private final MethodInfo methodInfo;

	public MethodExecuted(ControllerMethod method, MethodInfo methodInfo) {
		this.controllerMethod = method;
		this.methodInfo = methodInfo;
	}

	public ControllerMethod getControllerMethod() {
		return controllerMethod;
	}

	public Type getMethodReturnType() {
		return getControllerMethod().getMethod().getGenericReturnType();
	}

	public MethodInfo getMethodInfo() {
		return methodInfo;
	}
}