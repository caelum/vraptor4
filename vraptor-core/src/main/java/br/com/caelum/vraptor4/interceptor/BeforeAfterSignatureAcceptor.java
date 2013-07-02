package br.com.caelum.vraptor4.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.BeforeCall;

public class BeforeAfterSignatureAcceptor implements SignatureAcceptor {

	@Override
	public boolean accepts(Method method){
		return !new AroundSignatureAcceptor().accepts(method);
	}

	public String errorMessage() {
		return BeforeCall.class.getSimpleName()+" or "+AfterCall.class.getSimpleName()+" method must not receive "+InterceptorStack.class.getName()+" or "+SimpleInterceptorStack.class.getName();
	}
}
