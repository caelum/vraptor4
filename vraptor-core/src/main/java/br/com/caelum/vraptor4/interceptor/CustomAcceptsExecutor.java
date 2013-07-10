package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.util.List;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class CustomAcceptsExecutor implements StepExecutor<Boolean> {

	private StepInvoker stepInvoker;
	private Container container;
	private ControllerMethod controllerMethod;
	private ControllerInstance controllerInstance;

	public CustomAcceptsExecutor(StepInvoker stepInvoker, Container container,ControllerMethod controllerMethod,
			ControllerInstance controllerInstance) {
		super();
		this.stepInvoker = stepInvoker;
		this.container = container;
		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
	}
	

	@Override
	public boolean accept(Class<?> interceptorClass){
		List<Annotation> constraints = CustomAcceptsVerifier.getCustomAcceptsAnnotations(interceptorClass);		
		return !constraints.isEmpty();
	}

	@Override
	public Boolean execute(Object interceptor) {
		boolean customAccepts = new CustomAcceptsVerifier(controllerMethod,
				controllerInstance, container, interceptor).isValid();
		if (!customAccepts) {
			stepInvoker.tryToInvoke(interceptor,
					CustomAcceptsFailCallback.class);
		}
		return customAccepts;
	}
}