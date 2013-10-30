package br.com.caelum.vraptor.interceptor;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class DoNothingStepExecutor implements StepExecutor {

	@Override
	public boolean accept() {
		return true;
	}

	@Override
	public void execute(Object interceptor) {
	}

}
