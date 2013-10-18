package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class AroundExecutor implements StepExecutor<Object> {

	private final StepInvoker stepInvoker;
	private final InterceptorMethodParametersResolver parametersResolver;
	private Method method;

	public AroundExecutor(StepInvoker stepInvoker, InterceptorMethodParametersResolver parametersResolver,
			Method method, Class<?> interceptorClass) {
		this.stepInvoker = stepInvoker;
		this.parametersResolver = parametersResolver;
		this.method = method;
	}

	@Override
	public boolean accept(Class<?> interceptorClass) {
		return method != null;
	}

	@Override
	public Object execute(Object interceptor) {
		Object[] params = parametersResolver.parametersFor(this.method);
		return stepInvoker.tryToInvoke(interceptor, method, params);
	}
}
