package br.com.caelum.vraptor.interceptor;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;


public class NoStackParamValidationRuleTest {

	private NoStackParamValidationRule validationRule;

	@Before
	public void setUp() {
		validationRule = new NoStackParamValidationRule();
	}

	@Test(expected = IllegalArgumentException.class)
	public void mustReceiveStackAsParameterForAroundCall() {
		validationRule.validate(AroundInterceptorWithoutSimpleStackParameter.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void mustNotReceiveStackAsParameterForAcceptsCall() {
		validationRule.validate(AcceptsInterceptorWithStackAsParameter.class);

	}

	@Test(expected = IllegalArgumentException.class)
	public void mustNotReceiveStackAsParameterForBeforeAfterCall() {
		validationRule.validate(BeforeAfterInterceptorWithStackAsParameter.class);
	}

	@Intercepts
	class AroundInterceptorWithoutSimpleStackParameter {
		@AroundCall
		public void intercept() {}
	}

	@Intercepts
	public class AcceptsInterceptorWithStackAsParameter {
		@Accepts
		boolean accepts(SimpleInterceptorStack stack){
			return true;
		}
	}

	@Intercepts
	class BeforeAfterInterceptorWithStackAsParameter{
		@BeforeCall
		public void before(InterceptorStack interceptorStack) {}
	}
}