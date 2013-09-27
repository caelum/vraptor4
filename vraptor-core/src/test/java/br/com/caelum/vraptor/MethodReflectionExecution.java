package br.com.caelum.vraptor;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.cache.VRaptorDefaultCache;
import br.com.caelum.vraptor.core.MethodHandleExecutor;
import br.com.caelum.vraptor.interceptor.AllMethodHandles;
import br.com.caelum.vraptor.interceptor.InterceptorMethodsCache;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor.interceptor.StepInvoker;
import br.com.caelum.vraptor.interceptor.example.InterceptorWithCustomizedAccepts;

import static org.mockito.Mockito.mock;

public class MethodReflectionExecution {

	private static int runs;

	public static void stopWatcher(String name, int runs, Runnable runnable) {
		long t1 = System.currentTimeMillis();
		runnable.run();
		System.out.printf("%s;%d;%d\n", name, runs,
				(System.currentTimeMillis() - t1));
	}

	public static void main(String[] args) throws NoSuchMethodException,
			SecurityException, IllegalAccessException {
		final SimpleInterceptorStack stack = mock(SimpleInterceptorStack.class);
		final InterceptorWithCustomizedAccepts instance = new InterceptorWithCustomizedAccepts();
		final Method method = InterceptorWithCustomizedAccepts.class.getMethod(
				"intercept", SimpleInterceptorStack.class,File.class);
		final File dir = new File("target");
		for (runs = 1000; runs < 2001; runs += 1000) {
			for (int i = 0; i < 1; i++) {
				stopWatcher("Reflection", runs, new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < runs; i++) {
							try {
								method.invoke(instance, stack,dir);
							} catch (IllegalAccessException
									| IllegalArgumentException
									| InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		}
	}

}
