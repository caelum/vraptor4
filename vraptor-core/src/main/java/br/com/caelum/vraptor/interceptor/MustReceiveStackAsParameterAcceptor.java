package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.core.InterceptorStack;

public class MustReceiveStackAsParameterAcceptor {

	public static boolean accepts(Method method){

		for (Class<?> parameterType : method.getParameterTypes()) {
			if (SimpleInterceptorStack.class.isAssignableFrom(parameterType)
				|| InterceptorStack.class.isAssignableFrom(parameterType)) {
				return true;
			}
		}
		return false;
	}

	public static String errorMessage() {
		return AroundCall.class.getSimpleName()+" method must receive "+InterceptorStack.class.getName()+" or "+SimpleInterceptorStack.class.getName();
	}
}