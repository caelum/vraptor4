package br.com.caelum.vraptor.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;

import com.headius.invokebinder.Binder;

/**
 * Create MethodHandle for some Method.
 * @author Alberto Souza
 *
 */
@ApplicationScoped
public class MethodHandleFactory {

	
	/**
	 * Creates a MethodHandle for some method.
	 * 
	 * @param type The method receiver
	 * @param method The reflection representation of the method
	 * @return The MethodHandle representation
	 */
	public MethodHandle create(Class<?> type, Method method) {
		if (method == null) {
			return null;
		}

		try {
			Class<?>[] parameterTypes = method.getParameterTypes();

			MethodType description = MethodType.methodType(method.getReturnType(), parameterTypes);

			Lookup lookup = MethodHandles.lookup();

			MethodHandle originalHandle = lookup.findVirtual(type, method.getName(), description);

			Binder binder = createBinder(method, parameterTypes.length).cast(originalHandle.type());

			return binder.invokeVirtual(lookup, method.getName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Creates a generic Binder for methods
	 * @param method
	 * @param numberOfArguments
	 * @return
	 */
	private Binder createBinder(Method method, int numberOfArguments) {
		return Binder.from(Object.class, Object.class, Object[].class).spread(numberOfArguments);
	}
}
