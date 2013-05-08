package br.com.caelum.vraptor.ioc.cdi.extensions;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.cdi.ScopeInfo;
import br.com.caelum.vraptor.ioc.cdi.ScopesUtil;

@SuppressWarnings("rawtypes")
public class ComponentExtension {

	private AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();

	public ComponentExtension(AnnotatedTypeBuilder builder) {
		this.builder = builder;
	}

	@SuppressWarnings({"unchecked"})
	public void processAnnotatedType(final ProcessAnnotatedType pat) {
		final AnnotatedType defaultType = pat.getAnnotatedType();
		if (pat.getAnnotatedType().getJavaClass()
				.isAnnotationPresent(Component.class)) {
			builder.readFromType(defaultType);
			ScopesUtil registry = new ScopesUtil();
			ScopeInfo scopeInfoFromTheClass = registry.isScoped(defaultType
					.getJavaClass());
			if (!scopeInfoFromTheClass.hasScope()) {
				builder.addToClass(new ScopeInfo(RequestScoped.class)
						.getLiteral());
				AnnotatedType annotatedType = builder.create();
				pat.setAnnotatedType(annotatedType);
			}
		}
	}
}
