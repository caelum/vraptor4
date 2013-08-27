package br.com.caelum.vraptor4.interceptor;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.interceptor.example.ExampleOfSimpleStackInterceptor;
import br.com.caelum.vraptor4.interceptor.example.InterceptorWithInheritance;
import br.com.caelum.vraptor4.interceptor.example.WeldProxy$$$StyleInterceptor;

public class StepInvokerTest {
	
	private StepInvoker stepInvoker = new StepInvoker();

	@Ignore
	public void shouldNotReadInheritedMethods() throws Exception {
		stepInvoker.findMethod(AroundCall.class,InterceptorWithInheritance.class);		
	}
	
	@Test
	public void shouldFindFirstMethodAnnotatedWithInterceptorStep(){
		ExampleOfSimpleStackInterceptor proxy = spy(new ExampleOfSimpleStackInterceptor());
		assertNotNull(stepInvoker.findMethod(AroundCall.class, proxy.getClass()));
	}
	
	@Test
	public void shouldFindMethodFromWeldStyleInterceptor() throws SecurityException, NoSuchMethodException{
		assertNotNull(stepInvoker.findMethod(AroundCall.class, WeldProxy$$$StyleInterceptor.class));
	}
	
	@Test(expected=InterceptionException.class)
	public void shouldWrapMirrorException() throws SecurityException, NoSuchMethodException {
		assertNotNull(stepInvoker.findMethod(BeforeCall.class, ExceptionThrowerInterceptor.class));
		stepInvoker.tryToInvoke(new ExceptionThrowerInterceptor(), BeforeCall.class);
	}
	
}
