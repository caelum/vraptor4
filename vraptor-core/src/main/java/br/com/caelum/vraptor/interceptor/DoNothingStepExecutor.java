package br.com.caelum.vraptor.interceptor;

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
