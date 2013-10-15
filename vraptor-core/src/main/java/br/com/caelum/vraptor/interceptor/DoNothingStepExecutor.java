package br.com.caelum.vraptor.interceptor;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class DoNothingStepExecutor implements StepExecutor<Void> {

	@Override
	public boolean accept() {
		return true;
	}

	@Override
	public Void execute(Object interceptor) {
		return null;
	}

}
