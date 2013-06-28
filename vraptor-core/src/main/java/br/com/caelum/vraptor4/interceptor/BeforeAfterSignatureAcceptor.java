package br.com.caelum.vraptor4.interceptor;

import java.lang.reflect.Method;

public class BeforeAfterSignatureAcceptor implements SignatureAcceptor {

	@Override
	public boolean accepts(Method method){
		return !new AroundSignatureAcceptor().accepts(method);
	}
}
