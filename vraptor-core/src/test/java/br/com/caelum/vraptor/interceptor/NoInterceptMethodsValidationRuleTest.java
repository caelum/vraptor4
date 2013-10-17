package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import net.vidageek.mirror.list.dsl.MirrorList;

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
		StepInvoker stepInvoker = new StepInvoker(null);
		Class<?> type = SimpleInterceptor.class;
		MirrorList<Method> allMethods = stepInvoker.findAllMethods(type);
		new NoInterceptMethodsValidationRule(stepInvoker).validate(type, allMethods);
	}
}