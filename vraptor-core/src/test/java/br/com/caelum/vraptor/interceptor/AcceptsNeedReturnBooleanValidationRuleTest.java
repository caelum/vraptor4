package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

import net.vidageek.mirror.list.dsl.MirrorList;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;

public class AcceptsNeedReturnBooleanValidationRuleTest {

	private AcceptsNeedReturnBooleanValidationRule validationRule;
	private StepInvoker stepInvoker;

	@Before
	public void setUp() {
		stepInvoker = new StepInvoker(null);
		validationRule = new AcceptsNeedReturnBooleanValidationRule(stepInvoker);
	}

	@Test(expected = InterceptionException.class)
	public void shouldVerifyIfAcceptsMethodReturnsVoid() {
		Class<VoidAcceptsInterceptor> type = VoidAcceptsInterceptor.class;
		MirrorList<Method> allMethods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, allMethods);
	}

	@Test(expected = InterceptionException.class)
	public void shouldVerifyIfAcceptsMethodReturnsNonBooleanType() {
		Class<NonBooleanAcceptsInterceptor> type = NonBooleanAcceptsInterceptor.class;
		MirrorList<Method> allMethods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, allMethods);
	}

	@Intercepts @Vetoed
	static class VoidAcceptsInterceptor {
		@Accepts public void accepts(){}
	}

	@Intercepts @Vetoed
	static class NonBooleanAcceptsInterceptor{
		@Accepts public String accepts() { return ""; }
	}
}