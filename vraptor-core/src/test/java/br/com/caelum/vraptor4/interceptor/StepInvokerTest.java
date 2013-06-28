package br.com.caelum.vraptor4.interceptor;

import org.junit.Test;

import br.com.caelum.vraptor4.BeforeCall;

public class StepInvokerTest {
	
	private StepInvoker stepInvoker = new StepInvoker();

	@Test
	public void shouldNotReadInheritedMethods() throws Exception {
		stepInvoker.findMethod(BeforeCall.class,new InterceptorWithInheritance());
	}
}
