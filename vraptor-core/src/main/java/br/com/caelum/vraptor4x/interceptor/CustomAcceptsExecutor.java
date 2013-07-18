package br.com.caelum.vraptor4x.interceptor;

import java.lang.annotation.Annotation;
import java.util.List;

import br.com.caelum.vraptor4.ioc.Container;
import br.com.caelum.vraptor4.restfulie.controller.ControllerInstance;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;

public class CustomAcceptsExecutor implements StepExecutor<Boolean> {

	private StepInvoker stepInvoker;
	private Container container;

	public CustomAcceptsExecutor(StepInvoker stepInvoker, Container container) {
		super();
		this.stepInvoker = stepInvoker;
		this.container = container;
	}
	

	@Override
	public boolean accept(Class<?> interceptorClass){
		List<Annotation> constraints = CustomAcceptsVerifier.getCustomAcceptsAnnotations(interceptorClass);		
		return !constraints.isEmpty();
	}

	@Override
	public Boolean execute(Object interceptor) {
		boolean customAccepts = new CustomAcceptsVerifier(container.instanceFor(ControllerMethod.class),
				container.instanceFor(ControllerInstance.class), container, interceptor).isValid();
		if (!customAccepts) {
			stepInvoker.tryToInvoke(interceptor,
					CustomAcceptsFailCallback.class);
		}
		return customAccepts;
	}
}