package br.com.caelum.vraptor.interceptor;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;

@Vetoed
public class StackNextExecutor implements StepExecutor<Void> {

	private Container container;

	public StackNextExecutor(Container container) {
		this.container = container;
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
