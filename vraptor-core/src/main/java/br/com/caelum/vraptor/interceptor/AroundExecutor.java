package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

public class AroundExecutor implements StepExecutor<Object> {

	private final StepInvoker stepInvoker;
	private final InterceptorMethodParametersResolver parametersResolver;
	private Method method;

	public AroundExecutor(StepInvoker stepInvoker,
			InterceptorMethodParametersResolver parametersResolver,
			Method method, Class<?> interceptorClass) {
		this.stepInvoker = stepInvoker;
		this.parametersResolver = parametersResolver;
		this.method = method;
	}

	public boolean accept(Class<?> interceptorClass) {
		if (method != null) {
			MustReceiveStackAsParameterAcceptor stackAcceptor = new MustReceiveStackAsParameterAcceptor();
			if (!stackAcceptor.accepts(method)) {
				throw new IllegalArgumentException(method.getDeclaringClass().getCanonicalName() + " - " + stackAcceptor.errorMessage());
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
