package br.com.caelum.vraptor.ioc.cdi.extensions;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.cdi.ComponentFactoryAnnotatedTypeBuilderCreator;

@SuppressWarnings("rawtypes")
public class ComponentFactoryExtension{

	private AnnotatedTypeBuilder builder;

	public ComponentFactoryExtension(AnnotatedTypeBuilder builder) {
		super();
		this.builder = builder;
	}

	@SuppressWarnings({ "unchecked" })
	public void addProducesToComponentFactory(ProcessAnnotatedType pat) {
		final AnnotatedType defaultType = pat.getAnnotatedType();
		Class javaClass = defaultType.getJavaClass();
		if (ComponentFactory.class.isAssignableFrom(javaClass)) {
			new ComponentFactoryAnnotatedTypeBuilderCreator(builder)
					.create(javaClass);			
		}
	}
}
