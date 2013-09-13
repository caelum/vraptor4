package br.com.caelum.vraptor4.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.VRaptorException;

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
		InternalAcceptsSignature internalAcceptsSignature = new InternalAcceptsSignature(
				new NoStackParameterSignatureAcceptor());
		if (method != null) {
			if (!internalAcceptsSignature.accepts(method)) {
				throw new VRaptorException(internalAcceptsSignature.errorMessage());
			}
			return true;
		}
		return false;
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