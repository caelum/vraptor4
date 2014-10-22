package br.com.caelum.vraptor.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import net.vidageek.mirror.dsl.Mirror;

/**
 * Default implementation for {@link ReflectionProvider} that uses Mirror to access Java reflection featues.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.2
 */
@ApplicationScoped
public class DefaultReflectionProvider implements ReflectionProvider {

	public List<Method> getMethodsFor(Class<?> clazz) {
		return new Mirror().on(clazz).reflectAll().methods();
	}

	public Method getMethod(Class<?> clazz, String method, Class<?>... args) {
		return new Mirror().on(clazz).reflect().method(method).withArgs(args);
	}

	public Object invoke(Object instance, Method method, Object... args) {
		return new Mirror().on(instance).invoke().method(method).withArgs(args);
	}

	public Object invoke(Object instance, String methodName, Object... args) {
		return new Mirror().on(instance).invoke().method(methodName).withArgs(args);
	}

	public Object invokeGetter(Object instance, String fieldName) {
		return new Mirror().on(instance).invoke().getterFor(fieldName);
	}

	public List<Field> getFieldsFor(Class<?> type) {
		return new Mirror().on(type).reflectAll().fields();
	}

	public Field getField(Class<?> type, String fieldName) {
		return new Mirror().on(type).reflect().field(fieldName);
	}
}
