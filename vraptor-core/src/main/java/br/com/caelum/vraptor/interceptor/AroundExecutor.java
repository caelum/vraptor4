package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class AroundExecutor implements StepExecutor {

	private final StepInvoker stepInvoker;
	private final InterceptorMethodParametersResolver parametersResolver;
	private final Method method;

	public AroundExecutor(StepInvoker stepInvoker, InterceptorMethodParametersResolver parametersResolver, Method method) {
		this.stepInvoker = stepInvoker;
		this.parametersResolver = parametersResolver;
		this.method = method;
	}

	@Override
	public boolean accept() {
		return method != null;
	}

	@Override
	public void execute(Object interceptor) {
		Object[] params = parametersResolver.parametersFor(this.method);
		stepInvoker.tryToInvoke(interceptor, method, params);
	}
}