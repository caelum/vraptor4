package br.com.caelum.vraptor.interceptor;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class StackNextExecutor implements StepExecutor {

	private SimpleInterceptorStack simpleInterceptorStack;

	public StackNextExecutor(SimpleInterceptorStack simpleInterceptorStack) {
		this.simpleInterceptorStack = simpleInterceptorStack;
	}

	@Override
	public void execute(Object interceptor) {
		simpleInterceptorStack.next();
	}
}