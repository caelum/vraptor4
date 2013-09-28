package br.com.caelum.vraptor.factory;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.cache.LRUCache;
import br.com.caelum.vraptor.interceptor.StepInvoker;
import br.com.caelum.vraptor.reflection.DefaultMethodExecutor;
import br.com.caelum.vraptor.reflection.MethodExecutor;
import br.com.caelum.vraptor.reflection.MethodHandleFactory;

public class Factories {

	public static StepInvoker createStepInvoker(){
		return new StepInvoker(Factories.createMethodExecutor());		
	}
	
	public static MethodExecutor createMethodExecutor(){
		MethodHandleFactory methodHandleFactory = new MethodHandleFactory();
		LRUCache<Method, MethodHandle> cache = new LRUCache<Method,MethodHandle>(500);
		return new DefaultMethodExecutor(cache,methodHandleFactory); 		
	}	
}
