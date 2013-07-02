package br.com.caelum.vraptor4.interceptor;

import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.interceptor.StepInvoker;
import br.com.caelum.vraptor4.interceptor.example.ExampleOfSimpleStackInterceptor;
import br.com.caelum.vraptor4.interceptor.example.InterceptorWithInheritance;
import br.com.caelum.vraptor4.interceptor.example.WeldProxy$$$StyleInterceptor;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.spy;

public class StepInvokerTest {
	
	private StepInvoker stepInvoker = new StepInvoker();

	@Ignore
	public void shouldNotReadInheritedMethods() throws Exception {
		stepInvoker.findMethod(AroundCall.class,new InterceptorWithInheritance());		
	}
	
	@Test
	public void shouldFindFirstMethodAnnotatedWithInterceptorStep(){
		ExampleOfSimpleStackInterceptor proxy = spy(new ExampleOfSimpleStackInterceptor());
		assertNotNull(stepInvoker.findMethod(AroundCall.class,proxy));
	}
	
	@Test
	public void teste() throws SecurityException, NoSuchMethodException{
		WeldProxy$$$StyleInterceptor proxy = new WeldProxy$$$StyleInterceptor();
		assertNotNull(stepInvoker.findMethod(AroundCall.class,proxy));
	}	
	
	
}
