package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

public interface SignatureAcceptor {

	boolean accepts(Method method);

	String errorMessage();
	
}