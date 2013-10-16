package br.com.caelum.vraptor.interceptor;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;

public class AcceptsNeedReturnBooleanValidationRuleTest {

	private AcceptsNeedReturnBooleanValidationRule validationRule;

	@Before
	public void setUp() {
		validationRule = new AcceptsNeedReturnBooleanValidationRule();
	}

	@Test(expected = InterceptionException.class)
	public void shouldVerifyIfAcceptsMethodReturnsVoid() {
		validationRule.validate(VoidAcceptsInterceptor.class);
	}

	@Test(expected = InterceptionException.class)
	public void shouldVerifyIfAcceptsMethodReturnsNonBooleanType() {
		validationRule.validate(NonBooleanAcceptsInterceptor.class);
	}

	@Intercepts
	class VoidAcceptsInterceptor {
		@Accepts public void accepts(){}
	}

	@Intercepts
	class NonBooleanAcceptsInterceptor{
		@Accepts public String accepts() { return ""; }
	}
}