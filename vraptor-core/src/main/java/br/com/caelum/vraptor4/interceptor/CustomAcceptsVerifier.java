package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor4.ioc.Container;
import br.com.caelum.vraptor4.restfulie.controller.ControllerInstance;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;

public class CustomAcceptsVerifier {

	private static class AcceptsConstraintMatcher implements Matcher<Annotation> {

		@Override
		public boolean accepts(Annotation element) {
			return element.annotationType().isAnnotationPresent(AcceptsConstraint.class);
		}

	}

	private ControllerInstance controllerInstance;
	private Container container;
	private Object interceptor;
	private ControllerMethod controllerMethod;

	public CustomAcceptsVerifier(ControllerMethod controllerMethod,ControllerInstance controllerInstance,
			Container container, Object interceptor) {
		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
		this.container = container;
		this.interceptor = interceptor;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean isValid() {
		List<Annotation> constraints = CustomAcceptsVerifier.getCustomAcceptsAnnotations(interceptor.getClass());
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
	
	public static List<Annotation> getCustomAcceptsAnnotations(Class<?> klass){
		MirrorList<Annotation> constraints = new Mirror().on((AnnotatedElement) klass).reflectAll()
				.annotations().matching(new AcceptsConstraintMatcher());		
		return constraints;
	}

}
