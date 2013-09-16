package br.com.caelum.vraptor.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import java.lang.reflect.Method;

import org.junit.Test;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.example.ExampleOfSimpleStackInterceptor;
import br.com.caelum.vraptor.interceptor.example.InterceptorWithInheritance;
import br.com.caelum.vraptor.interceptor.example.WeldProxy$$$StyleInterceptor;

public class StepInvokerTest {

	private StepInvoker stepInvoker = new StepInvoker();

	@Test
	public void shouldNotReadInheritedMethods() throws Exception {
		Class<?> interceptorClass = InterceptorWithInheritance.class;
		Method method = stepInvoker.findMethod(BeforeCall.class,interceptorClass);
		assertEquals(method, interceptorClass.getDeclaredMethod("begin"));
	}

	@Test(expected=IllegalStateException.class)
	public void shouldThrowsExceptionWhenInterceptorHasMoreThanOneAnnotatedMethod() {
		stepInvoker.findMethod(BeforeCall.class,InterceptorWithMoreThanOneBeforeCallMethod.class);
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
		Method method = stepInvoker.findMethod(BeforeCall.class, ExceptionThrowerInterceptor.class);
		assertNotNull(method);
		stepInvoker.tryToInvoke(new ExceptionThrowerInterceptor(), method);
	}
}
