package br.com.caelum.vraptor.interceptor;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class StackNextExecutor implements StepExecutor<Void> {

	private SimpleInterceptorStack simpleInterceptorStack;

	public StackNextExecutor(SimpleInterceptorStack simpleInterceptorStack) {
		this.simpleInterceptorStack = simpleInterceptorStack;
	}

	@Override
	public boolean accept() {
		return true;
	}

	@Override
	public Void execute(Object interceptor) {
		simpleInterceptorStack.next();
		return null;
	}
}