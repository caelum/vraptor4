package br.com.caelum.vraptor.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import java.lang.reflect.Method;

import net.vidageek.mirror.list.dsl.MirrorList;

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
		MirrorList<Method> methods = stepInvoker.findAllMethods(interceptorClass);
		Method method = stepInvoker.findMethod(methods, BeforeCall.class,interceptorClass);
		assertEquals(method, interceptorClass.getDeclaredMethod("begin"));
	}

	@Test(expected=IllegalStateException.class)
	public void shouldThrowsExceptionWhenInterceptorHasMoreThanOneAnnotatedMethod() {
		Class<?> interceptorClass = InterceptorWithMoreThanOneBeforeCallMethod.class;
		MirrorList<Method> methods = stepInvoker.findAllMethods(interceptorClass);
		stepInvoker.findMethod(methods, BeforeCall.class, interceptorClass);
	}

	@Test
	public void shouldFindFirstMethodAnnotatedWithInterceptorStep(){
		ExampleOfSimpleStackInterceptor proxy = spy(new ExampleOfSimpleStackInterceptor());
		MirrorList<Method> methods = stepInvoker.findAllMethods(proxy.getClass());
		assertNotNull(stepInvoker.findMethod(methods, AroundCall.class, proxy.getClass()));
	}

	@Test
	public void shouldFindMethodFromWeldStyleInterceptor() throws SecurityException, NoSuchMethodException{
		Class<?> interceptorClass = WeldProxy$$$StyleInterceptor.class;
		MirrorList<Method> methods = stepInvoker.findAllMethods(interceptorClass);
		assertNotNull(stepInvoker.findMethod(methods, AroundCall.class, interceptorClass));
	}

	@Test(expected=InterceptionException.class)
	public void shouldWrapMirrorException() throws SecurityException, NoSuchMethodException {
		Class<ExceptionThrowerInterceptor> interceptorClass = ExceptionThrowerInterceptor.class;
		MirrorList<Method> methods = stepInvoker.findAllMethods(interceptorClass);
		Method method = stepInvoker.findMethod(methods, BeforeCall.class, interceptorClass);
		assertNotNull(method);
		stepInvoker.tryToInvoke(new ExceptionThrowerInterceptor(), method);
	}
}