package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.Accepts;

public class InterceptorAcceptsExecutor {

	private StepInvoker stepInvoker;
	private Container container;
	private InterceptorMethodParameterResolver parameterResolver;

	public InterceptorAcceptsExecutor(StepInvoker stepInvoker,
			Container container) {
		super();
		this.stepInvoker = stepInvoker;
		this.container = container;
		this.parameterResolver = new InterceptorMethodParameterResolver(stepInvoker, container);
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
