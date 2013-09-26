package br.com.caelum.vraptor.interceptor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.cache.VRaptorCache;

//TODO unit tests
@ApplicationScoped
public class InterceptorMethodsCache {

	private StepInvoker stepInvoker;
	private VRaptorCache<Class<?>, AllMethodHandles> cache;
	
	
	@Deprecated
	public InterceptorMethodsCache(){}

	@Inject
	public InterceptorMethodsCache(StepInvoker stepInvoker,
			VRaptorCache<Class<?>, AllMethodHandles> cache) {
		super();
		this.stepInvoker = stepInvoker;
		this.cache = cache;
	}

	public void put(Class<?> type) {
		cache.put(type, buildAllMethodHandles(type));
	}

	private AllMethodHandles buildAllMethodHandles(Class<?> type) {
		MirrorList<Method> methods = stepInvoker.findAllMethods(type);
		Method around = stepInvoker.findMethod(methods, AroundCall.class, type);
		Method before = stepInvoker.findMethod(methods, BeforeCall.class, type);
		Method after = stepInvoker.findMethod(methods, AfterCall.class, type);
		Method accepts = stepInvoker.findMethod(methods, Accepts.class, type);
		AllMethodHandles handles = new AllMethodHandles();
		handles.add(AroundCall.class,buildMethodHandle(type,around));
		handles.add(BeforeCall.class,buildMethodHandle(type,before));
		handles.add(AfterCall.class,buildMethodHandle(type,after));
		handles.add(Accepts.class,buildMethodHandle(type,accepts));
		return handles;

	}

	private MethodHandle buildMethodHandle(Class<?> type, Method method) {
		if(method==null){
			return null;
		}
		MethodHandle methodHandle;
		MethodType description = MethodType.methodType(method.getReturnType(),
				method.getParameterTypes());

		try {
			methodHandle = MethodHandles.lookup().findVirtual(type,
					method.getName(), description);
			return methodHandle;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
