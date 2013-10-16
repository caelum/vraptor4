package br.com.caelum.vraptor.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.LRU;

import com.google.common.base.Throwables;

/**
 * This class should use method handle to invoke methods.
 * 
 * @author Alberto Souza
 * 
 */
@ApplicationScoped
public class DefaultMethodExecutor implements MethodExecutor {

	private final CacheStore<Method,MethodHandle> cache;
	private final MethodHandleFactory methodHandleFactory;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultMethodExecutor() {
		this(null, null);
	}

	@Inject
	public DefaultMethodExecutor(@LRU(capacity=500) CacheStore<Method, MethodHandle> cache,
			MethodHandleFactory methodHandleFactory) {
		this.cache = cache;
		this.methodHandleFactory = methodHandleFactory;
	}

	@Override
	public <T> T invoke(final Method method, final Object instance, Object... args) {
		Callable<MethodHandle> newMethodHandleIfNotExists = new Callable<MethodHandle>() {

			@Override
			public MethodHandle call() throws Exception {
				return methodHandleFactory.create(instance.getClass(), method);
			}
		};
		MethodHandle methodHandle = cache.fetch(method,newMethodHandleIfNotExists);
		try {
			return (T) methodHandle.invokeExact(instance, args);
		} catch (Throwable e) {
			Throwables.propagateIfPossible(e);
			throw new MethodExecutorException(e);
		}
	}

}
