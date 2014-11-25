package br.com.caelum.vraptor.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.exception.MirrorException;

/**
 * Default implementation for {@link ReflectionProvider} that uses Mirror to access Java reflection features.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.2
 */
@ApplicationScoped
public class DefaultReflectionProvider implements ReflectionProvider {

	@Override
	public List<Method> getMethodsFor(Class<?> clazz) {
		try {
			return new Mirror().on(clazz).reflectAll().methods();
		} catch (MirrorException e) {
			throw new ReflectionProviderException(e.getCause());
		}
	}

	@Override
	public Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		try {
			return new Mirror().on(clazz).reflect().method(methodName).withArgs(parameterTypes);
		} catch (MirrorException e) {
			throw new ReflectionProviderException(e.getCause());
		}
	}

	@Override
	public Object invoke(Object instance, Method method, Object... args) {
		try {
			return new Mirror().on(instance).invoke().method(method).withArgs(args);
		} catch (MirrorException e) {
			throw new ReflectionProviderException(e.getCause());
		}
	}

	@Override
	public Object invoke(Object instance, String methodName, Object... args) {
		try {
			return new Mirror().on(instance).invoke().method(methodName).withArgs(args);
		} catch (MirrorException e) {
			throw new ReflectionProviderException(e.getCause());
		}
	}

	@Override
	public Object invokeGetter(Object instance, String fieldName) {
		try {
			return new Mirror().on(instance).invoke().getterFor(fieldName);
		} catch (MirrorException e) {
			throw new ReflectionProviderException(e.getCause());
		}
	}

	@Override
	public List<Field> getFieldsFor(Class<?> clazz) {
		try {
			return new Mirror().on(clazz).reflectAll().fields();
		} catch (MirrorException e) {
			throw new ReflectionProviderException(e.getCause());
		}
	}

	@Override
	public Field getField(Class<?> clazz, String fieldName) {
		try {
			return new Mirror().on(clazz).reflect().field(fieldName);
		} catch (MirrorException e) {
			throw new ReflectionProviderException(e.getCause());
		}
	}
}
