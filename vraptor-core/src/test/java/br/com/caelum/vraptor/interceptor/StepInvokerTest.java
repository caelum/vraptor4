package br.com.caelum.vraptor.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.vidageek.mirror.list.dsl.MirrorList;

import org.junit.Test;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.factory.Factories;
import br.com.caelum.vraptor.interceptor.example.ExampleOfSimpleStackInterceptor;
import br.com.caelum.vraptor.interceptor.example.InterceptorWithInheritance;
import br.com.caelum.vraptor.interceptor.example.WeldProxy$$$StyleInterceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.spy;

public class StepInvokerTest {

	private StepInvoker stepInvoker = Factories.createStepInvoker();
	
	@Test
	public void shouldNotReadInheritedMethods() throws Exception {
		Class<?> interceptorClass = InterceptorWithInheritance.class;
		Method method = findMethod(interceptorClass, BeforeCall.class);
		assertEquals(method, interceptorClass.getDeclaredMethod("begin"));
	}

	@Test(expected=IllegalStateException.class)
	public void shouldThrowsExceptionWhenInterceptorHasMoreThanOneAnnotatedMethod() {
		Class<?> interceptorClass = InterceptorWithMoreThanOneBeforeCallMethod.class;
		findMethod(interceptorClass, BeforeCall.class);
	}

	@Test
	public void shouldFindFirstMethodAnnotatedWithInterceptorStep(){
		ExampleOfSimpleStackInterceptor proxy = spy(new ExampleOfSimpleStackInterceptor());
		findMethod(proxy.getClass(), BeforeCall.class);
	}

	@Test
	public void shouldFindMethodFromWeldStyleInterceptor() throws SecurityException, NoSuchMethodException{
		Class<?> interceptorClass = WeldProxy$$$StyleInterceptor.class;
		assertNotNull(findMethod(interceptorClass, AroundCall.class));
	}

	private Method findMethod(Class<?> interceptorClass, Class<? extends Annotation> step) {
		MirrorList<Method> methods = stepInvoker.findAllMethods(interceptorClass);
		Method method = stepInvoker.findMethod(methods, step, interceptorClass);
		return method;
	}
}