package br.com.caelum.vraptor.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Utility class to execute reflection operations.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.2
 */
public interface ReflectionProvider {

	List<Method> getMethodsFor(Class<?> clazz);

	Method getMethod(Class<?> clazz, String method, Class<?>... args);

	Object invoke(Object instance, Method method, Object... args);

	Object invoke(Object instance, String methodName, Object... args);

	Object invokeGetter(Object instance, String fieldName);

	List<Field> getFieldsFor(Class<?> type);

	Field getField(Class<?> type, String fieldName);
}