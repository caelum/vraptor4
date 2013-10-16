package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.VRaptorException;

import com.google.common.base.Objects;

@Vetoed
public class InterceptorAcceptsExecutor implements StepExecutor<Boolean>{

	private final StepInvoker stepInvoker;
	private final InterceptorMethodParametersResolver parameterResolver;
	private final Method method;

	public InterceptorAcceptsExecutor(StepInvoker stepInvoker, InterceptorMethodParametersResolver parameterResolver, 
			Method method) {
		this.stepInvoker = stepInvoker;
		this.parameterResolver = parameterResolver;
		this.method = method;
	}

	@Override
	public boolean accept() {
		if (method == null) {
			return false;
		}

		if(!method.getReturnType().equals(Boolean.class) && !method.getReturnType().equals(boolean.class)) {
			throw new VRaptorException("@Accepts method must return boolean");
		}

		SignatureAcceptor acceptor = new NoStackParameterSignatureAcceptor();
		if (!acceptor.accepts(method)) {
			throw new VRaptorException(method.getDeclaringClass().getCanonicalName() + " - " + acceptor.errorMessage());
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
