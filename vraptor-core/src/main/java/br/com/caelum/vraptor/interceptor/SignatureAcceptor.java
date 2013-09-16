package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

public interface SignatureAcceptor {

	public abstract boolean accepts(Method method);

	public abstract String errorMessage();
	
}