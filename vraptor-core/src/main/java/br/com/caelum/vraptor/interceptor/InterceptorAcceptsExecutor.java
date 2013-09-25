package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.VRaptorException;

import com.google.common.base.Objects;

public class InterceptorAcceptsExecutor implements StepExecutor<Boolean>{

	private StepInvoker stepInvoker;
	private InterceptorMethodParametersResolver parameterResolver;
	private Method method;

	public InterceptorAcceptsExecutor(StepInvoker stepInvoker,
			InterceptorMethodParametersResolver parameterResolver,
			Method method, Class<?> interceptorClass) {

		this.stepInvoker = stepInvoker;
		this.parameterResolver = parameterResolver;
		this.method = method;
	}

	public boolean accept(Class<?> interceptorClass) {
		if (method == null) return false;

		if(!method.getReturnType().equals(Boolean.class)
				&& !method.getReturnType().equals(boolean.class)) {
			throw new VRaptorException("@Accepts method must return boolean");
		}
		SignatureAcceptor acceptor = new NoStackParameterSignatureAcceptor();
		if (!acceptor.accepts(method)) {
			throw new VRaptorException(method.getDeclaringClass().getCanonicalName() + " - " + acceptor.errorMessage());
		}
		return true;
	}

	public Boolean execute(Object interceptor) {
		if(method != null) {
			Object[] params = parameterResolver.parametersFor(method);
			Object returnObject = stepInvoker.tryToInvoke(interceptor, method, params);
			return Objects.firstNonNull((Boolean) returnObject, false);
		}
		return true;
	}
}
