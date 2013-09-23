package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.VRaptorException;

import com.google.common.base.Objects;

public class InterceptorAcceptsExecutor implements StepExecutor<Boolean>{

	private StepInvoker stepInvoker;
	private InterceptorMethodParametersResolver parameterResolver;
	private Method method;

	public InterceptorAcceptsExecutor(StepInvoker stepInvoker,
			InterceptorMethodParametersResolver parameterResolver,
			Class<?> interceptorClass) {
		this.stepInvoker = stepInvoker;
		this.parameterResolver = parameterResolver;
		method = stepInvoker.findMethod(Accepts.class, interceptorClass);
	}

	public boolean accept(Class<?> interceptorClass) {
		if (method == null) return false;

		if(!method.getReturnType().equals(Boolean.class)
				&& !method.getReturnType().equals(boolean.class)) {
			throw new VRaptorException("@Accepts method must return boolean");
		}
		NoStackParameterSignatureAcceptor acceptor = new NoStackParameterSignatureAcceptor();
		if (!acceptor.accepts(method)) {
			throw new VRaptorException(acceptor.errorMessage());
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
