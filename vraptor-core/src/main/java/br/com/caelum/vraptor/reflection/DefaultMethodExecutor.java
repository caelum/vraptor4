package br.com.caelum.vraptor.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.cache.LRU;
import br.com.caelum.vraptor.cache.VRaptorCache;

import com.google.common.base.Throwables;

/**
 * This class should use method handle to invoke methods.
 * 
 * @author Alberto Souza
 * 
 */
@ApplicationScoped
public class DefaultMethodExecutor implements MethodExecutor {

	private VRaptorCache<Method,MethodHandle> cache;
	private MethodHandleFactory methodHandleFactory;

	@Inject
	public DefaultMethodExecutor(@LRU(capacity=500) VRaptorCache<Method, MethodHandle> cache,
			MethodHandleFactory methodHandleFactory) {
		this.cache = cache;
		this.methodHandleFactory = methodHandleFactory;
	}

	@Deprecated
	public DefaultMethodExecutor() {
	}

	@Override
	public <T> T invoke(Method method, Object instance, Object... args) {
		//TODO change to the new way of using lazy evaluation in cache.
		MethodHandle methodHandle = cache.get(method);
		if (methodHandle == null) {
			methodHandle = methodHandleFactory.create(instance.getClass(), method);
			cache.put(method, methodHandle);
		}
		try {
			return (T) methodHandle.invokeExact(instance, args);
		} catch (Throwable e) {
			Throwables.propagateIfPossible(e);
			throw new MethodExecutorException(e);
		}
	}

}
