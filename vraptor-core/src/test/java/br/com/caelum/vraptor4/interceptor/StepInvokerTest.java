package br.com.caelum.vraptor4.interceptor;

import org.junit.Test;

import br.com.caelum.vraptor4.AroundCall;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.spy;

public class StepInvokerTest {
	
	private StepInvoker stepInvoker = new StepInvoker();

	@Test
	//TODO mudar o nome para alguma coisa que nao sei agora
	public void shouldNotReadInheritedMethods() throws Exception {
		stepInvoker.findMethod(AroundCall.class,new InterceptorWithInheritance());		
	}
	
	@Test
	public void teste() throws SecurityException, NoSuchMethodException{
		ExampleOfSimpleStackInterceptor wtfProxy = spy(new ExampleOfSimpleStackInterceptor());
		assertNotNull(stepInvoker.findMethod(AroundCall.class,wtfProxy));
	}
	
	
}
