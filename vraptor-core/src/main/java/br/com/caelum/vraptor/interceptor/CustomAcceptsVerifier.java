package br.com.caelum.vraptor.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
public class CustomAcceptsVerifier {

	private final Container container;

	/**
	 * @deprecated CDI eyes only
	 */
	protected CustomAcceptsVerifier() {
		this(null);
	}

	@Inject
	public CustomAcceptsVerifier(Container container) {
		this.container = container;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean isValid(Object interceptor, ControllerMethod controllerMethod,
			ControllerInstance controllerInstance, List<Annotation> constraints) {

		for (Annotation annotation : constraints) {
			AcceptsConstraint constraint = annotation.annotationType().getAnnotation(AcceptsConstraint.class);
			Class<? extends AcceptsValidator<?>> validatorClass = constraint.value();
			AcceptsValidator validator = container.instanceFor(validatorClass);
			validator.initialize(annotation);
			if (!validator.validate(controllerMethod, controllerInstance)) {
				return false;
			}
		}
		return true;
	}

	private static class AcceptsConstraintMatcher implements Matcher<Annotation> {
		@Override
		public boolean accepts(Annotation element) {
			return element.annotationType().isAnnotationPresent(AcceptsConstraint.class);
		}
	}

	public static List<Annotation> getCustomAcceptsAnnotations(Class<?> clazz){
		return new Mirror().on((AnnotatedElement) clazz).reflectAll()
				.annotations().matching(new AcceptsConstraintMatcher());
	}
}