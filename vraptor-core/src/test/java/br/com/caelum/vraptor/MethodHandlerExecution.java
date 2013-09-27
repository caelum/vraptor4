package br.com.caelum.vraptor;

import java.io.File;
import java.lang.invoke.MethodHandle;

import br.com.caelum.vraptor.cache.VRaptorDefaultCache;
import br.com.caelum.vraptor.core.MethodHandleExecutor;
import br.com.caelum.vraptor.interceptor.AllMethodHandles;
import br.com.caelum.vraptor.interceptor.InterceptorMethodsCache;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor.interceptor.StepInvoker;
import br.com.caelum.vraptor.interceptor.example.InterceptorWithCustomizedAccepts;

import static org.mockito.Mockito.mock;

public class MethodHandlerExecution {

	private static int runs;

	public static void stopWatcher(String name, int runs, Runnable runnable) {
		long t1 = System.currentTimeMillis();
		runnable.run();
		System.out.printf("%s;%d;%d\n", name, runs,
				(System.currentTimeMillis() - t1));
	}

	public static void main(String[] args) throws NoSuchMethodException,
			SecurityException, IllegalAccessException {
		InterceptorMethodsCache cache = new InterceptorMethodsCache(
				new StepInvoker(),
				new VRaptorDefaultCache<Class<?>, AllMethodHandles>());
		cache.put(InterceptorWithCustomizedAccepts.class);
		final InterceptorWithCustomizedAccepts instance = new InterceptorWithCustomizedAccepts();
		final MethodHandle handle = cache.get(
				InterceptorWithCustomizedAccepts.class, AroundCall.class);
		final SimpleInterceptorStack stack = mock(SimpleInterceptorStack.class);
		final MethodHandleExecutor handleExecutor = new MethodHandleExecutor(
				handle);		
		final File dir = new File("target");
		for (runs = 1000; runs < 2001; runs += 1000) {
			for (int i = 0; i < 1; i++) {
				stopWatcher("MethodHandle", runs, new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < runs; i++) {
							handleExecutor.invoke(instance, stack,dir);
						}
					}
				});
			}
		}
	}

}
