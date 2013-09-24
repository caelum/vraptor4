package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.core.InterceptorStack;

public class NoStackParameterSignatureAcceptor {

	public static boolean accepts(Method method){
		return !MustReceiveStackAsParameterAcceptor.accepts(method);
	}

	public static String errorMessage() {
		return "Non @Around method must not receive "+InterceptorStack.class.getName()+" or "+SimpleInterceptorStack.class.getName();
	}
}
