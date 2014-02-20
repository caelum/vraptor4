package br.com.caelum.vraptor.http;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Provides parameter names for a {@link Method} or {@link Constructor}. This class is really not necessary
 * since JDK 8 can discovery natively. But keep easily to maintain a branch compatible with JDK 1.7 that needs
 * Paranamer.
 * 
 * @author Otávio Scherer Garcia
 */
public class JavaParameterNameProvider implements ParameterNameProvider {

	@Override
	public Parameter[] parametersFor(AccessibleObject executable) {
		checkState(executable instanceof Executable, "Only methods or constructors are available");
		return ((Executable) executable).getParameters();
	}
}
