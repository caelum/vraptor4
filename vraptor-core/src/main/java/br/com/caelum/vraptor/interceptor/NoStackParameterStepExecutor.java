package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

public class NoStackParameterStepExecutor implements StepExecutor<Void> {

	private StepInvoker stepInvoker;
	private Method method;

	public NoStackParameterStepExecutor(StepInvoker stepInvoker,
			Method method, Class<?> interceptorClass) {
		this.stepInvoker = stepInvoker;
		this.method = method;
	}

	public boolean accept(Class<?> interceptorClass) {
		NoStackParameterSignatureAcceptor noStackAcceptor = new NoStackParameterSignatureAcceptor();
		if (method != null) {
			if (!noStackAcceptor.accepts(method)) {
				throw new IllegalArgumentException(noStackAcceptor.errorMessage());
			}
			return true;
		}
		return false;
	}

	public Void execute(Object interceptor) {
		stepInvoker.tryToInvoke(interceptor, method);
		return null;
	}
}
