package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class AroundExecutor {

	private StepInvoker stepInvoker;
	private InterceptorStack stack;
	private Container container;

	public AroundExecutor(StepInvoker stepInvoker, InterceptorStack stack,
			Container container) {
		super();
		this.stepInvoker = stepInvoker;
		this.stack = stack;
		this.container = container;
	}

	public void execute(Object interceptor, ControllerMethod controllerMethod,
			ControllerInstance controllerInstance) {
		if (noAround(interceptor)) {
			stack.next(controllerMethod, controllerInstance.getController());
		} else {
			stepInvoker.tryToInvoke(
					interceptor,
					AroundCall.class,
					new AroundSignatureAcceptor(),
					parametersFor(AroundCall.class, interceptor,
							container));
		}
	}

	private boolean noAround(Object interceptor) {
		return stepInvoker.findMethod(AroundCall.class, interceptor) == null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object[] parametersFor(Class<? extends Annotation> step,
			Object interceptor, Container container) {
		Method methodToInvoke = stepInvoker.findMethod(step, interceptor);
		if (methodToInvoke == null)
			return new Object[] {};
		Class<?>[] parameterTypes = methodToInvoke.getParameterTypes();
		ArrayList parameters = new ArrayList();
		for (Class<?> parameterType : parameterTypes) {
			parameters.add(container.instanceFor(parameterType));
		}
		return parameters.toArray();

	}
}
