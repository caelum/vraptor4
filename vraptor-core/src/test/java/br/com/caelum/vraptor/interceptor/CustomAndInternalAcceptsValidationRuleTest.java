package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import net.vidageek.mirror.list.dsl.MirrorList;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.factory.Factories;
import br.com.caelum.vraptor.interceptor.example.NotLogged;

public class CustomAndInternalAcceptsValidationRuleTest {

	private CustomAndInternalAcceptsValidationRule validationRule;
	private StepInvoker stepInvoker;

	@Before
	public void setUp() {
		stepInvoker = Factories.createStepInvoker();
		validationRule = new CustomAndInternalAcceptsValidationRule(stepInvoker);
	}

	@Test(expected = IllegalStateException.class)
	public void mustNotUseInternalAcceptsAndCustomAccepts(){
		Class<?> type = InternalAndCustomAcceptsInterceptor.class;
		MirrorList<Method> methods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, methods);
	}

	@Test
	public void shouldValidateIfConstainsOnlyInternalAccepts(){
		Class<?> type = InternalAcceptsInterceptor.class;
		MirrorList<Method> methods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, methods);
	}

	@Test
	public void shouldValidateIfConstainsOnlyCustomAccepts(){
		Class<?> type = CustomAcceptsInterceptor.class;
		MirrorList<Method> methods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, methods);
	}

	@AcceptsWithAnnotations(NotLogged.class)
	public class InternalAndCustomAcceptsInterceptor {
		@Accepts public boolean accepts(){ return true; }
	}

	public class InternalAcceptsInterceptor {
		@Accepts public boolean accepts(){ return true; }
	}

	@AcceptsWithAnnotations(NotLogged.class)
	public class CustomAcceptsInterceptor {
	}
}