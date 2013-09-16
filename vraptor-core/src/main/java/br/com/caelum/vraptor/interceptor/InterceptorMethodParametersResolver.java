package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;

import br.com.caelum.vraptor.ioc.Container;

public class InterceptorMethodParametersResolver {

	private Container container;

	public InterceptorMethodParametersResolver(Container container) {
		super();
		this.container = container;
	}

	@SuppressWarnings("unchecked")
	public Object[] parametersFor(Method methodToInvoke) {
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
