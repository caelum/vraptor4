package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor4.controller.ControllerMethod;

class InterceptorStackDecorator implements InterceptorStack {

	private InterceptorStack delegate;
	private boolean nexted;

	public InterceptorStackDecorator(InterceptorStack stack) {
		this.delegate = stack;
	}

	public void next(ControllerMethod method, Object resourceInstance)
			throws InterceptionException {
		this.nexted = true;
		delegate.next(method, resourceInstance);
	}

	public void add(Class<? extends Interceptor> interceptor) {
		delegate.add(interceptor);
	}

	public void addAsNext(Class<? extends Interceptor> interceptor) {
		delegate.addAsNext(interceptor);
	}
	
	public boolean isNexted() {
		return nexted;
	}

}
