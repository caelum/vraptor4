package br.com.caelum.vraptor4x.interceptor;

import java.lang.reflect.Method;

public class InternalAcceptsSignature implements SignatureAcceptor {

	private SignatureAcceptor signatureAcceptor;
	private String errorMessage = "@Accepts method must return boolean";

	public InternalAcceptsSignature(SignatureAcceptor signatureAcceptor) {
		super();
		this.signatureAcceptor = signatureAcceptor;
	}

	@Override
	public boolean accepts(Method method) {
		if (!signatureAcceptor.accepts(method)) {
			this.errorMessage = signatureAcceptor.errorMessage();
			return false;
		}
		if (!method.getReturnType().equals(Boolean.class)
				&& !method.getReturnType().equals(boolean.class)) {
			return false;
		}
		return true;
	}

	@Override
	public String errorMessage() {
		return this.errorMessage;
	}

}
