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
	private StepInvoker invoker;

	@Before
	public void setUp() {
		invoker = new StepInvoker(null);
		validationRule = new NoStackParamValidationRule(invoker);
	}

	@Test(expected = IllegalStateException.class)
	public void mustReceiveStackAsParameterForAroundCall() {
		Class<?> type = AroundInterceptorWithoutSimpleStackParameter.class;
		validationRule.validate(type, invoker.findAllMethods(type));
	}

	@Test(expected = IllegalStateException.class)
	public void mustNotReceiveStackAsParameterForAcceptsCall() {
		Class<?> type = AcceptsInterceptorWithStackAsParameter.class;
		validationRule.validate(type, invoker.findAllMethods(type));

	}

	@Test(expected = IllegalStateException.class)
	public void mustNotReceiveStackAsParameterForBeforeAfterCall() {
		Class<?> type = BeforeAfterInterceptorWithStackAsParameter.class;
		validationRule.validate(type, invoker.findAllMethods(type));
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