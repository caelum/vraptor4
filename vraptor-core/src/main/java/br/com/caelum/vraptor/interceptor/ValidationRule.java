package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import net.vidageek.mirror.list.dsl.MirrorList;

public interface ValidationRule {

	public void validate(Class<?> originalType, MirrorList<Method> allMethods);
}
