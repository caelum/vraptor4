package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import net.vidageek.mirror.list.dsl.MirrorList;

/**
 * A validation rule interface for new interceptors.
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
public interface ValidationRule {

	/**
	 * @param originalType - Interceptor type to be validated
	 * @param allMethods - MirrorList of all interceptor methods
	 */
	public void validate(Class<?> originalType, MirrorList<Method> allMethods);
}