package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor4.Accepts;

public class InterceptorAcceptsExecutor {

	private StepInvoker stepInvoker;
	private InterceptorMethodParametersResolver parameterResolver;

	public InterceptorAcceptsExecutor(StepInvoker stepInvoker,
			InterceptorMethodParametersResolver parameterResolver) {
		super();
		this.stepInvoker = stepInvoker;
		this.parameterResolver = parameterResolver;
	}

	public boolean execute(Object interceptor) {
		boolean interceptorAccepts = true;
		Object returnObject = stepInvoker.tryToInvoke(interceptor,
				Accepts.class, new NoStackParameterSignatureAcceptor(),
				parameterResolver.parametersFor(Accepts.class, interceptor));
		if (returnObject != null) {
			if (!returnObject.getClass().equals(Boolean.class)) {
				throw new IllegalStateException(
						"@Accepts method should return boolean");
			}
			interceptorAccepts = (Boolean) returnObject;
		}
		return interceptorAccepts;
	}
}
