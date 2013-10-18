package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import net.vidageek.mirror.list.dsl.MirrorList;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;

public class NoInterceptMethodsValidationRuleTest {

	private StepInvoker stepInvoker;

	@Intercepts
	class SimpleInterceptor {
		public void dummyMethodWithoutInterceptorAnnotations() {}
	}

	@Intercepts
	class SimpleInterceptorWithCallableMethod {
		@AfterCall public void afterCall() {}
	}

	@Before
	public void setUp() {
		this.stepInvoker = new StepInvoker(null);
	}

	@Test(expected=InterceptionException.class)
	public void shoulThrowExceptionIfInterceptorDontHaveAnyCallableMethod() {
		Class<?> type = SimpleInterceptor.class;
		MirrorList<Method> allMethods = stepInvoker.findAllMethods(type);
		new NoInterceptMethodsValidationRule(stepInvoker).validate(type, allMethods);
	}

	@Test
	public void shoulWorksFineIfInterceptorHaveAtLeastOneCallableMethod() {
		Class<?> type = SimpleInterceptorWithCallableMethod.class;
		MirrorList<Method> allMethods = stepInvoker.findAllMethods(type);
		new NoInterceptMethodsValidationRule(stepInvoker).validate(type, allMethods);
	}
}