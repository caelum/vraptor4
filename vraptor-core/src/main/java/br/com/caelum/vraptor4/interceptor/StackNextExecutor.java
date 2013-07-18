package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.ioc.Container;
import br.com.caelum.vraptor4.restfulie.controller.ControllerInstance;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;

public class StackNextExecutor implements StepExecutor<Void> {

	private Container container;

	public StackNextExecutor(Container container) {
		this.container = container;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean accept(Class<?> interceptorClass) {
		return true;
	}

	@Override
	public Void execute(Object interceptor) {
		InterceptorStack stack = container.instanceFor(InterceptorStack.class);
		ControllerMethod controllerMethod = container.instanceFor(ControllerMethod.class);
		ControllerInstance controllerInstance = container.instanceFor(ControllerInstance.class);
		stack.next(controllerMethod , controllerInstance.getController());
		return null;
	}

}
