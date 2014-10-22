package br.com.caelum.vraptor.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Class to execute reflection operations.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.2
 */
public interface ReflectionProvider {

	List<Method> getMethodsFor(Class<?> clazz);

	Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes);

	Object invoke(Object instance, Method method, Object... args);

	Object invoke(Object instance, String methodName, Object... args);

	Object invokeGetter(Object instance, String fieldName);

	List<Field> getFieldsFor(Class<?> clazz);

	Field getField(Class<?> clazz, String fieldName);
}