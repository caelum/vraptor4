package br.com.caelum.vraptor;

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

public class DirectMethodCallExecution {

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
		final Method method = InterceptorWithCustomizedAccepts.class.getMethod(
				"intercept", SimpleInterceptorStack.class);

		Lookup lookup = MethodHandles.lookup();

		MethodType description = MethodType.methodType(void.class,
				SimpleInterceptorStack.class);
		final MethodHandle originalHandle = lookup.findVirtual(
				InterceptorWithCustomizedAccepts.class, "intercept",
				description);
		for (runs = 1000; runs < 100001; runs += 1000) {
			for (int i = 0; i < 100; i++) {
				stopWatcher("MethodHandle", runs, new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < runs; i++) {
							handleExecutor.invoke(instance, stack);
						}
					}
				});

				stopWatcher("StaticMethodHandle", runs, new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < runs; i++) {
							try {
								originalHandle.invokeExact(instance, stack);
							} catch (Throwable e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});

				stopWatcher("Reflection", runs, new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < runs; i++) {
							try {
								method.invoke(instance, stack);
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
