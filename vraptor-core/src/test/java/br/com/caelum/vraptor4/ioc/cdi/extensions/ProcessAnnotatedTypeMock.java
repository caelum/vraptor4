package br.com.caelum.vraptor4.ioc.cdi.extensions;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

@SuppressWarnings("rawtypes")
public class ProcessAnnotatedTypeMock implements ProcessAnnotatedType {

	private AnnotatedType annotatedType;
	private boolean veto;

	public ProcessAnnotatedTypeMock(AnnotatedType annotatedType) {
		super();
		this.annotatedType = annotatedType;
	}

	public AnnotatedType getAnnotatedType() {
		return annotatedType;
	}

	public void setAnnotatedType(AnnotatedType type) {
		this.annotatedType = type;
	}

	public void veto() {
		this.veto = true;
	}
	
	public boolean isVeto() {
		return veto;
	}

}
