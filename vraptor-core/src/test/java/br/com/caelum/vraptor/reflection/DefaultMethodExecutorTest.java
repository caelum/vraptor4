package br.com.caelum.vraptor.reflection;

import java.lang.reflect.Method;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import br.com.caelum.vraptor.factory.Factories;

public class DefaultMethodExecutorTest {

	private MethodExecutor executor = Factories.createMethodExecutor();

	@Test
	public void shouldCreateMethodHandle() throws Throwable {
		Method method = Example.class.getDeclaredMethod("method");
		executor.invoke(method,new Example());
	}
	
	@Test
	public void shouldCreateMethodHandle2() throws Throwable {
		Method method = Example.class.getDeclaredMethod("method2",String.class);
		Object result = executor.invoke(method,new Example(),"testing");
		assertEquals("testing",result);
	}	
	
	static class Example{
		public void method(){
			
		}
		
		public String method2(String param){
			return param;
		}
	}
}
