package br.com.caelum.vraptor4.interceptor;

import java.lang.reflect.Method;

public class AlwaysTrueSignatureAcceptor implements SignatureAcceptor{

	@Override
	public boolean accepts(Method method) {
		return true;
	}

	public String errorMessage() {
		return null;
	}

}
