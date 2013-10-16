package br.com.caelum.vraptor.interceptor;

import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;

public class NoInterceptMethodsValidationRuleTest {

	@Intercepts
	class SimpleInterceptor {
		public void dummyMethodWithoutInterceptorAnnotations() {}
	}

	@Test(expected=InterceptionException.class)
	public void shoulThrowExceptionIfInterceptorDontHaveAnyCallableMethod() {
		new NoInterceptMethodsValidationRule().validate(SimpleInterceptor.class);
	}
}