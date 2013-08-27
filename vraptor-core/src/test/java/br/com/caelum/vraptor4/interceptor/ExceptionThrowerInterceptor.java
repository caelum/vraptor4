package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor4.BeforeCall;

public class ExceptionThrowerInterceptor {
	@BeforeCall
	public void intercept() {
		throw new RuntimeException();
	}
}