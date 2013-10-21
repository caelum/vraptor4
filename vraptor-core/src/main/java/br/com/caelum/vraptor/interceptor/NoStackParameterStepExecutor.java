package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class NoStackParameterStepExecutor implements StepExecutor<Void> {

	private StepInvoker stepInvoker;
	private Method method;

	public NoStackParameterStepExecutor(StepInvoker stepInvoker, Method method) {
		this.stepInvoker = stepInvoker;
		this.method = method;
	}

	@Override
	public boolean accept() {
		return method != null;
	}

	@Override
	public Void execute(Object interceptor) {
		stepInvoker.tryToInvoke(interceptor, method);
		return null;
	}
}