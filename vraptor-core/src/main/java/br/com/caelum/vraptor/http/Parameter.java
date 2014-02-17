package br.com.caelum.vraptor.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Class that represents a method or constructor parameter.
 * 
 * @author Otávio Scherer Garcia
 */
public class Parameter {

	private final int index;
	private final String name;
	private final AccessibleObject holder;
	private final Type parameterizedType;
	private final Class<?> parameterType;
	private final Annotation[] annotations;

	public Parameter(int index, String name, AccessibleObject holder) {
		this.index = index;
		this.name = name;
		this.holder = holder;

		if (holder instanceof Method) {
			Method method = (Method) holder;
			parameterizedType = method.getGenericParameterTypes()[index];
			parameterType = method.getParameterTypes()[index];
			annotations = method.getParameterAnnotations()[index];
		} else if (holder instanceof Constructor) {
			Constructor<?> constr = (Constructor<?>) holder;
			parameterizedType = constr.getGenericParameterTypes()[index];
			parameterType = constr.getParameterTypes()[index];
			annotations = constr.getParameterAnnotations()[index];
		} else {
			throw new UnsupportedOperationException("We can only evaluate methods or constructors " + holder.getClass());
		}
	}

	public String getName() {
		return name;
	}

	public Type getParameterizedType() {
		return parameterizedType;
	}

	public Class<?> getType() {
		return parameterType;
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> clazz) {
		return getAnnotation(clazz) != null;
	}

	public <T extends Annotation> T getAnnotation(Class<T> clazz) {
		for (Annotation a : getDeclaredAnnotations()) {
			if (a.annotationType().equals(clazz)) {
				return clazz.cast(a);
			}
		}
		return null;
	}

	public Annotation[] getAnnotations() {
		return getDeclaredAnnotations();
	}

	public Annotation[] getDeclaredAnnotations() {
		return annotations;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Parameter) {
			Parameter other = (Parameter) obj;
			return other.index == index && other.holder.equals(holder);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, holder);
	}
}