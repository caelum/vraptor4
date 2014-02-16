package br.com.caelum.vraptor.factory;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.cache.LRUCacheStore;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.interceptor.StepInvoker;
import br.com.caelum.vraptor.reflection.DefaultMethodExecutor;
import br.com.caelum.vraptor.reflection.MethodExecutor;
import br.com.caelum.vraptor.reflection.MethodHandleFactory;

public class Factories {

	public static StepInvoker createStepInvoker(){
		return new StepInvoker(Factories.createMethodExecutor());		
	}

	public static ParameterNameProvider createParameterNameProvider() {
		CacheStore<AccessibleObject, Parameter[]> cache = new DefaultCacheStore<>();
		return new ParanamerNameProvider(cache);
	}

	public static MethodExecutor createMethodExecutor(){
		MethodHandleFactory methodHandleFactory = new MethodHandleFactory();
		LRUCacheStore<Method, MethodHandle> cache = new LRUCacheStore<>(500);
		return new DefaultMethodExecutor(cache,methodHandleFactory); 		
	}	
}
