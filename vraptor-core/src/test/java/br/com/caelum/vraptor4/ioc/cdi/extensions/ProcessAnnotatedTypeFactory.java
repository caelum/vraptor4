package br.com.caelum.vraptor4.ioc.cdi.extensions;

import javax.enterprise.inject.spi.AnnotatedType;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;


public class ProcessAnnotatedTypeFactory {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ProcessAnnotatedTypeMock create(Class<?> klass) {
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();
		AnnotatedType annotatedType = builder.readFromType(klass).create();
		ProcessAnnotatedTypeMock pat = new ProcessAnnotatedTypeMock(annotatedType);
		return pat;
	}
}
