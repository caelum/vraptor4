package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.VRaptorException;

public class InterceptorAcceptsExecutor implements StepExecutor<Boolean>{

	private StepInvoker stepInvoker;
	private InterceptorMethodParametersResolver parameterResolver;

	public InterceptorAcceptsExecutor(StepInvoker stepInvoker,
			InterceptorMethodParametersResolver parameterResolver) {
		super();
		this.stepInvoker = stepInvoker;
		this.parameterResolver = parameterResolver;
	}

	public boolean accept(Class<?> interceptorClass) {
		Method acceptsMethod = stepInvoker.findMethod(Accepts.class,
				interceptorClass);
		InternalAcceptsSignature internalAcceptsSignature = new InternalAcceptsSignature(
				new NoStackParameterSignatureAcceptor());
		if (acceptsMethod != null) {
			if (!internalAcceptsSignature.accepts(acceptsMethod)) {
				throw new VRaptorException(
						internalAcceptsSignature.errorMessage());
			}
			return true;
		}
		return false;
	}

	public Boolean execute(Object interceptor) {
		boolean interceptorAccepts = true;
		Object returnObject = stepInvoker.tryToInvoke(interceptor,
				Accepts.class,
				parameterResolver.parametersFor(Accepts.class, interceptor));
		if (returnObject != null) {
			interceptorAccepts = (Boolean) returnObject;
		}
		return interceptorAccepts;
	}
}
