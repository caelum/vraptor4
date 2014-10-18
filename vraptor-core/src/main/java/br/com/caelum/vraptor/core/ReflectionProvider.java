package br.com.caelum.vraptor.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.inject.Vetoed;

import net.vidageek.mirror.dsl.Mirror;

/**
 * Utility class to execute reflection operations.
 * 
 * @author Otávio Scherer Garcia
 */
@Vetoed
public class ReflectionProvider {

	public static List<Method> getMethodsFor(Class<?> clazz) {
		return new Mirror().on(clazz).reflectAll().methods();
	}

	public static Method getMethod(Class<?> clazz, String method, Class<?>... args) {
		return new Mirror().on(clazz).reflect().method(method).withArgs(args);
	}

	public static Object invoke(Object instance, Method method, Object... args) {
		return new Mirror().on(instance).invoke().method(method).withArgs(args);
	}

	public static Object invoke(Object instance, String methodName, Object... args) {
		return new Mirror().on(instance).invoke().method(methodName).withArgs(args);
	}

	public static Object invokeGetter(Object instance, String fieldName) {
		return new Mirror().on(instance).invoke().getterFor(fieldName);
	}

	public static List<Field> getFieldsFor(Class<?> type) {
		return new Mirror().on(type).reflectAll().fields();
	}

	public static Field getField(Class<?> type, String fieldName) {
		return new Mirror().on(type).reflect().field(fieldName);
	}

	public static List<Annotation> getAnnotationsFor(Class<?> clazz) {
		return new Mirror().on((AnnotatedElement) clazz).reflectAll().annotations();
	}
}