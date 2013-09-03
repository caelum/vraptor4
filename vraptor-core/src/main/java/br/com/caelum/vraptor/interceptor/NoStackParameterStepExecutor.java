package br.com.caelum.vraptor.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class NoStackParameterStepExecutor implements StepExecutor<Void> {

	private StepInvoker stepInvoker;
	private Class<? extends Annotation> step;

	public NoStackParameterStepExecutor(StepInvoker stepInvoker,
			Class<? extends Annotation> step) {
		super();
		this.stepInvoker = stepInvoker;
		this.step = step;
	}

	public boolean accept(Class<?> interceptorClass) {
		NoStackParameterSignatureAcceptor noStackAcceptor = new NoStackParameterSignatureAcceptor();
		Method stepMethod = stepInvoker.findMethod(step, interceptorClass);
		if (stepMethod != null) {
			if (!noStackAcceptor.accepts(stepMethod)) {
				throw new IllegalArgumentException(
						noStackAcceptor.errorMessage());
			}
			return true;
		}
		return false;
	}

	public Void execute(Object interceptor) {
		stepInvoker.tryToInvoke(interceptor, step);
		return null;
	}

}
