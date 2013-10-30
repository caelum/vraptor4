package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class NoStackParameterStepExecutor implements StepExecutor {

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
	public void execute(Object interceptor) {
		if (method != null) {
			stepInvoker.tryToInvoke(interceptor, method);
		}
	}
}