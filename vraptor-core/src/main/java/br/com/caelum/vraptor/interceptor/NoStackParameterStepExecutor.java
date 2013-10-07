package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class NoStackParameterStepExecutor implements StepExecutor<Void> {

	private StepInvoker stepInvoker;
	private Method method;

	public NoStackParameterStepExecutor(StepInvoker stepInvoker,
			Method method, Class<?> interceptorClass) {
		this.stepInvoker = stepInvoker;
		this.method = method;
	}

	@Override
	public boolean accept(Class<?> interceptorClass) {
		NoStackParameterSignatureAcceptor noStackAcceptor = new NoStackParameterSignatureAcceptor();
		if (method != null) {
			if (!noStackAcceptor.accepts(method)) {
				throw new IllegalArgumentException(method.getDeclaringClass().getCanonicalName() + " - " + noStackAcceptor.errorMessage());
			}
			return true;
		}
		return false;
	}

	@Override
	public Void execute(Object interceptor) {
		stepInvoker.tryToInvoke(interceptor, method);
		return null;
	}
}
