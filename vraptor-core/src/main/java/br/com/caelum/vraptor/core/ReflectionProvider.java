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

	/**
	 * Returns all methods from the class, including methods declared in the interfaces and super classes.
	 */
	List<Method> getMethodsFor(Class<?> clazz);

	/**
	 * Returns the method by name with same signature as {@code parameterTypes}. The method will search in the class, and if not found, in
	 * the super classes. If the method is not found in any level, the {@code null} value will be returned.
	 */
	Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes);

	/**
	 * Invoke a method from current instance.
	 */
	Object invoke(Object instance, Method method, Object... args);

	/**
	 * Invoke a method from current instance.
	 */
	Object invoke(Object instance, String methodName, Object... args);

	/**
	 * Invoke a getter method from current instance.
	 */
	Object invokeGetter(Object instance, String fieldName);

	/**
	 * Returns all fields from the class, including fields declared in the interfaces and super classes.
	 */
	List<Field> getFieldsFor(Class<?> clazz);

	/**
	 * Returns the field by name. The field will search in the class, and if not found, in the super classes. If the field is not found in
	 * any level, the {@code null} value will be returned.
	 */
	Field getField(Class<?> clazz, String fieldName);
}
