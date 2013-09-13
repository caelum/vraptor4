package br.com.caelum.vraptor4.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.VRaptorException;

public class InterceptorAcceptsExecutor implements StepExecutor<Boolean>{

	private StepInvoker stepInvoker;
	private InterceptorMethodParametersResolver parameterResolver;
	private Method method;

	public InterceptorAcceptsExecutor(StepInvoker stepInvoker,
			InterceptorMethodParametersResolver parameterResolver,
			Class<?> interceptorClass) {
		super();
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
		boolean interceptorAccepts = true;
		Object returnObject = null;
		if(method != null) {
			Object[] params = parameterResolver.parametersFor(method);
			returnObject = stepInvoker.tryToInvoke(interceptor, method, params);
		}
		if (returnObject != null) {
			interceptorAccepts = (Boolean) returnObject;
		}
		return interceptorAccepts;
	}
}
