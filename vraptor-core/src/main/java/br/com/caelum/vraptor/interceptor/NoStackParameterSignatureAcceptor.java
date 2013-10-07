package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.core.InterceptorStack;

@Vetoed
public class NoStackParameterSignatureAcceptor implements SignatureAcceptor {

	@Override
	public boolean accepts(Method method){
		return !new MustReceiveStackAsParameterAcceptor().accepts(method);
	}

	public String errorMessage() {
		return "Non @Around method must not receive "+InterceptorStack.class.getName()+" or "+SimpleInterceptorStack.class.getName();
	}
}
