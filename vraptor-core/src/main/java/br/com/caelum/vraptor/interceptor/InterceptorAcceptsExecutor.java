package br.com.caelum.vraptor.interceptor;

import static com.google.common.base.Objects.firstNonNull;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InterceptorAcceptsExecutor {

	private final InterceptorMethodParametersResolver parameterResolver;
	private final StepInvoker invoker;

	/**
	 * @deprecated CDI eyes only
	 */
	protected InterceptorAcceptsExecutor() {
		this(null, null);
	}

	@Inject
	public InterceptorAcceptsExecutor(
			InterceptorMethodParametersResolver parameterResolver,
			StepInvoker invoker) {

		this.parameterResolver = parameterResolver;
		this.invoker = invoker;
	}

	public Boolean accepts(Object interceptor, Method method) {
		if(method != null) {
			Object[] params = parameterResolver.parametersFor(method);
			Object returnObject = invoker.tryToInvoke(interceptor, method, params);
			return firstNonNull((Boolean) returnObject, false);
		}
		return true;
	}
}