package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.BeforeCall;

public class ExceptionThrowerInterceptor {
	@BeforeCall
	public void intercept() {
		throw new RuntimeException();
	}
}