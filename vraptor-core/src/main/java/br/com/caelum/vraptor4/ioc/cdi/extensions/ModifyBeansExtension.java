package br.com.caelum.vraptor4.ioc.cdi.extensions;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

@SuppressWarnings("rawtypes")
public class ModifyBeansExtension implements Extension {

	@SuppressWarnings("unchecked")
	public void processAnnotatedType(@Observes final ProcessAnnotatedType pat) {
		if(accept(pat)){
			AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();		
			builder.readFromType(pat.getAnnotatedType());
			AddInjectToConstructorExtension addInjectToConstructorExtension = new AddInjectToConstructorExtension(builder);
			addInjectToConstructorExtension.processAnnotatedType(pat);
			pat.setAnnotatedType(builder.create());
		}
	}

	private boolean accept(ProcessAnnotatedType pat) {
		Class type = pat.getAnnotatedType().getJavaClass();
		return !type.isEnum() && !type.isInterface() && !type.isAnnotation();
	}

}
