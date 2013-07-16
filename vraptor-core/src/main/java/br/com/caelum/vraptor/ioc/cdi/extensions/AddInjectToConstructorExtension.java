package br.com.caelum.vraptor.ioc.cdi.extensions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import br.com.caelum.vraptor.core.BaseComponents;

import com.google.common.collect.Sets;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class AddInjectToConstructorExtension {

	private final AnnotatedTypeBuilder builder;

	public AddInjectToConstructorExtension(AnnotatedTypeBuilder builder) {
		this.builder = builder;
	}

	public void processAnnotatedType(@Observes final ProcessAnnotatedType pat) {
		HashSet<Class<? extends Annotation>> stereotypes = Sets
				.newHashSet(BaseComponents.getStereotypes());
		for (Class<? extends Annotation> stereotype : stereotypes) {
			for (Annotation foundAnnotation : pat.getAnnotatedType()
					.getAnnotations()) {
				if (foundAnnotation.annotationType().equals(stereotype)) {
					tryToDefineInjectConstructor(pat, builder);
					return;
				}
			}
		}
	}

	private void tryToDefineInjectConstructor(ProcessAnnotatedType pat,
			AnnotatedTypeBuilder builder) {
		Class componentClass = pat.getAnnotatedType().getJavaClass();
		List<Constructor> constructors = getConstructors(componentClass);
		boolean hasArgsConstructorAndNoInjection = !constructors.isEmpty();
		if (hasArgsConstructorAndNoInjection) {
			Constructor constructor = constructors.get(0);
			builder.addToConstructor(constructor,
					new AnnotationLiteral<Inject>() {
					});
		}
	}

	private MirrorList getConstructors(Class componentClass) {
		return new Mirror().on(componentClass).reflectAll()
			.constructors().matching(new ArgsAndNoInjectConstructorMatcher());
	}

	private static class ArgsAndNoInjectConstructorMatcher implements Matcher<Constructor> {

		public boolean accepts(Constructor constructor) {
			boolean hasInject = constructor.isAnnotationPresent(Inject.class);
			boolean hasParameters = constructor.getParameterTypes().length > 0;
			return !hasInject && hasParameters;
		}

	}
}
