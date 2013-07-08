package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class CustomAcceptsExecutor {

	private StepInvoker stepInvoker;
	private Container container;

	public CustomAcceptsExecutor(StepInvoker stepInvoker, Container container) {
		super();
		this.stepInvoker = stepInvoker;
		this.container = container;
	}

	public boolean execute(Object interceptor,
			ControllerMethod controllerMethod,
			ControllerInstance controllerInstance) {
		boolean customAccepts = new CustomAcceptsVerifier(controllerMethod,
				controllerInstance, container, interceptor).isValid();
		if (!customAccepts) {
			stepInvoker.tryToInvoke(interceptor,
					CustomAcceptsFailCallback.class);
		}
		return customAccepts;
	}
}