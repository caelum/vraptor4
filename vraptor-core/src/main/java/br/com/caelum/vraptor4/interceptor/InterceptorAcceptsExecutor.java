package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.Accepts;

public class InterceptorAcceptsExecutor {

	private StepInvoker stepInvoker;
	private Container container;

	public InterceptorAcceptsExecutor(StepInvoker stepInvoker,
			Container container) {
		super();
		this.stepInvoker = stepInvoker;
		this.container = container;
	}

	public boolean execute(Object interceptor) {
		boolean interceptorAccepts = true;
		Object returnObject = stepInvoker.tryToInvoke(interceptor,
				Accepts.class, new NoStackParameterSignatureAcceptor(),
				parametersFor(Accepts.class, interceptor, container));
		if (returnObject != null) {
			if (!returnObject.getClass().equals(Boolean.class)) {
				throw new IllegalStateException(
						"@Accepts method should return boolean");
			}
			interceptorAccepts = (Boolean) returnObject;
		}
		return interceptorAccepts;
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
			System.out.println(parameterType);
			parameters.add(container.instanceFor(parameterType));
		}
		return parameters.toArray();

	}
}
