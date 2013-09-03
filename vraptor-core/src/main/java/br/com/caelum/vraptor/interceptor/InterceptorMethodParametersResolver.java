package br.com.caelum.vraptor.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import br.com.caelum.vraptor.ioc.Container;

public class InterceptorMethodParametersResolver {

	private StepInvoker stepInvoker;
	private Container container;

	public InterceptorMethodParametersResolver(StepInvoker stepInvoker,
			Container container) {
		super();
		this.stepInvoker = stepInvoker;
		this.container = container;
	}

	@SuppressWarnings("unchecked")
	public Object[] parametersFor(Class<? extends Annotation> step,
			Object interceptor) {
		Method methodToInvoke = stepInvoker.findMethod(step, interceptor.getClass());
		if (methodToInvoke == null)
			return new Object[] {};
		Class<?>[] parameterTypes = methodToInvoke.getParameterTypes();
		@SuppressWarnings("rawtypes")
		ArrayList parameters = new ArrayList();
		for (Class<?> parameterType : parameterTypes) {
			parameters.add(container.instanceFor(parameterType));
		}
		return parameters.toArray();

	}
}
