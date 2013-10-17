package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

import com.google.common.base.Objects;

@Vetoed
public class InterceptorAcceptsExecutor implements StepExecutor<Boolean>{

	private StepInvoker stepInvoker;
	private InterceptorMethodParametersResolver parameterResolver;
	private Method method;

	public InterceptorAcceptsExecutor(StepInvoker stepInvoker, InterceptorMethodParametersResolver parameterResolver,
			Method method, Class<?> interceptorClass) {
		this.stepInvoker = stepInvoker;
		this.parameterResolver = parameterResolver;
		this.method = method;
	}

	@Override
	public boolean accept(Class<?> interceptorClass) {
		if (method == null) {
			return false;
		}
		return true;
	}

	@Override
	public Boolean execute(Object interceptor) {
		if(method != null) {
			Object[] params = parameterResolver.parametersFor(method);
			Object returnObject = stepInvoker.tryToInvoke(interceptor, method, params);
			return Objects.firstNonNull((Boolean) returnObject, false);
		}
		return true;
	}
}
