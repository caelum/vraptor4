package br.com.caelum.vraptor.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.ioc.Container;

@Vetoed
public class CustomAcceptsExecutor implements StepExecutor<Boolean> {

	private StepInvoker stepInvoker;
	private Container container;
	private Method method;
	private Class<?> interceptorClass;

	public CustomAcceptsExecutor(StepInvoker stepInvoker,
			Container container, Method method, Class<?> interceptorClass) {

		this.stepInvoker = stepInvoker;
		this.container = container;
		this.method = method;
		this.interceptorClass = interceptorClass;
	}

	@Override
	public boolean accept(){
		List<Annotation> constraints = CustomAcceptsVerifier.getCustomAcceptsAnnotations(interceptorClass);
		return !constraints.isEmpty();
	}

	@Override
	public Boolean execute(Object interceptor) {
		boolean customAccepts = new CustomAcceptsVerifier(container.instanceFor(ControllerMethod.class),
				container.instanceFor(ControllerInstance.class), container, interceptor).isValid();
		if (!customAccepts) {
			stepInvoker.tryToInvoke(interceptor, method);
		}
		return customAccepts;
	}
}