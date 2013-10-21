package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
public class InterceptorMethodParametersResolver {

	private final Container container;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected InterceptorMethodParametersResolver() {
		this(null);
	}

	@Inject
	public InterceptorMethodParametersResolver(Container container) {
		this.container = container;
	}

	public Object[] parametersFor(Method methodToInvoke) {
		if (methodToInvoke == null)
			return new Object[] {};
		Class<?>[] parameterTypes = methodToInvoke.getParameterTypes();
		List<Object> parameters = new ArrayList<>();
		for (Class<?> parameterType : parameterTypes) {
			parameters.add(container.instanceFor(parameterType));
		}
		return parameters.toArray();
	}
}