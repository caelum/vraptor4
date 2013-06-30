package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class CustomiAcceptsVerifier {

	public class AcceptsConstraintMatcher implements Matcher<Annotation> {

		@Override
		public boolean accepts(Annotation element) {
			return element.annotationType().isAnnotationPresent(AcceptsConstraint.class);
		}

	}

	private ControllerInstance controllerInstance;
	private Container container;
	private Object interceptor;
	private ControllerMethod controllerMethod;

	public CustomiAcceptsVerifier(ControllerMethod controllerMethod,ControllerInstance controllerInstance,
			Container container, Object interceptor) {
		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
		this.container = container;
		this.interceptor = interceptor;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean isValid() {
		MirrorList<Annotation> constraints = new Mirror().on((AnnotatedElement) interceptor.getClass()).reflectAll()
				.annotations().matching(new AcceptsConstraintMatcher());
		for (Annotation annotation : constraints) {
			AcceptsConstraint constraint = annotation.annotationType().getAnnotation(AcceptsConstraint.class);
			Class<? extends AcceptsValidator<?>> validatorClass = constraint.value();
			AcceptsValidator validator = container.instanceFor(validatorClass);
			validator.initialize(annotation);
			if(!validator.validate(controllerMethod,controllerInstance)){
				return false;
			}
		}
		return true;
	}

}
