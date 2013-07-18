package br.com.caelum.vraptor4.interceptor;

public class DoNothingStepExecutor implements StepExecutor<Void> {

	@Override
	public boolean accept(Class<?> interceptorClass) {
		return true;
	}

	@Override
	public Void execute(Object interceptor) {
		return null;
	}

}
