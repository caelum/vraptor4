package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.AroundCall;

public class AroundExecutor implements StepExecutor<Object> {

	private final StepInvoker stepInvoker;
	private final InterceptorMethodParametersResolver parametersResolver;
	private Method method;

	public AroundExecutor(StepInvoker stepInvoker,
			InterceptorMethodParametersResolver parametersResolver,
			Class<?> interceptorClass) {
		this.stepInvoker = stepInvoker;
		this.parametersResolver = parametersResolver;
		this.method = stepInvoker.findMethod(AroundCall.class,interceptorClass);
	}

	public boolean accept(Class<?> interceptorClass) {
		if (method != null) {
			if (!MustReceiveStackAsParameterAcceptor.accepts(method)) {
				throw new IllegalArgumentException(MustReceiveStackAsParameterAcceptor.errorMessage());
			}
			return true;
		}
		return false;
	}

	public Object execute(Object interceptor) {
		Object[] params = parametersResolver.parametersFor(this.method);
		return stepInvoker.tryToInvoke(interceptor, method, params);
	}
}
