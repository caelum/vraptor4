package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
public class InterceptorMethodParametersResolver {

	private Container container;

	@Deprecated // CDI eyes only
	public InterceptorMethodParametersResolver() {}

	@Inject
	public InterceptorMethodParametersResolver(Container container) {
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