package br.com.caelum.vraptor.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import net.vidageek.mirror.dsl.Mirror;

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
		return new Mirror().on(clazz).reflectAll().methods();
	}

	@Override
	public Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		return new Mirror().on(clazz).reflect().method(methodName).withArgs(parameterTypes);
	}

	@Override
	public Object invoke(Object instance, Method method, Object... args) {
		return new Mirror().on(instance).invoke().method(method).withArgs(args);
	}

	@Override
	public Object invoke(Object instance, String methodName, Object... args) {
		return new Mirror().on(instance).invoke().method(methodName).withArgs(args);
	}

	@Override
	public Object invokeGetter(Object instance, String fieldName) {
		return new Mirror().on(instance).invoke().getterFor(fieldName);
	}

	@Override
	public List<Field> getFieldsFor(Class<?> clazz) {
		return new Mirror().on(clazz).reflectAll().fields();
	}

	@Override
	public Field getField(Class<?> clazz, String fieldName) {
		return new Mirror().on(clazz).reflect().field(fieldName);
	}
}
