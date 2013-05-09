package br.com.caelum.vraptor.ioc.cdi.extensions;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

@SuppressWarnings("rawtypes")
public class ModifyComponentExtension implements Extension {

	@SuppressWarnings("unchecked")
	public void processAnnotatedType(@Observes final ProcessAnnotatedType pat) {
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();
		builder.readFromType(pat.getAnnotatedType());
		AddInjectToConstructorExtension addInjectToConstructorExtension = new AddInjectToConstructorExtension(builder);
		ComponentExtension componentExtension = new ComponentExtension(builder);
		addInjectToConstructorExtension.processAnnotatedType(pat);
		componentExtension.processAnnotatedType(pat);
		pat.setAnnotatedType(builder.create());
	}

}
