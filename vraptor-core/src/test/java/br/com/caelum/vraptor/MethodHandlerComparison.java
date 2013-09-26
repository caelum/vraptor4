package br.com.caelum.vraptor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodHandlerComparison {

	private static class MethodAccessExampleWithArgs {
		private final int i;
		private int counter;

		public MethodAccessExampleWithArgs(int i_) {
			i = i_;
		}

		public void bar(int j, String msg) {
			counter++;
		}
	}

	// Using Reflection
	public static Method makeMethod() {
		Method meth = null;

		try {
			Class<?>[] argTypes = new Class[] { int.class, String.class };

			meth = MethodAccessExampleWithArgs.class.getDeclaredMethod("bar",
					argTypes);

			meth.setAccessible(true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return meth;
	}

	// Using method handles
	public static MethodHandle makeMh() {
		MethodHandle mh;

		MethodType desc = MethodType.methodType(void.class, int.class,
				String.class);

		try {
			mh = MethodHandles.lookup().findVirtual(
					MethodAccessExampleWithArgs.class, "bar", desc);
			return mh;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	public static void stopWatcher(Runnable runnable){
		long t1 = System.currentTimeMillis();
		runnable.run();
		System.out.println("Time => "+(System.currentTimeMillis() - t1));
	}
	
	public static void main(String[] args) {
		final MethodAccessExampleWithArgs ex1 = new MethodAccessExampleWithArgs(0);
		final MethodAccessExampleWithArgs ex2 = new MethodAccessExampleWithArgs(1);
		final MethodHandle methodHandle = MethodHandlerComparison.makeMh();
		final Method method = MethodHandlerComparison.makeMethod();
		stopWatcher(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<10000;i++){
					try {
						method.invoke(ex1,i,"reflection");
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		});
		stopWatcher(new Runnable() {
			
			@Override
			public void run() {
				
				for(int i=0;i<10000;i++){
						try {
							methodHandle.invokeExact(ex2, i, "method handle");
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		});
		
		stopWatcher(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<10000;i++){
						try {
							ex2.bar(i,"blabla");
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		});		
	}
	
	
}
