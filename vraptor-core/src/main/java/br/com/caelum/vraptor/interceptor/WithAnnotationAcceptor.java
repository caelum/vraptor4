package br.com.caelum.vraptor.interceptor;

import static com.google.common.collect.Collections2.transform;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

import com.google.common.base.Function;

/**
 * Verify if certain annotations are presents in class or method.
 *
 * @author Alberto Souza
 *
 */
public class WithAnnotationAcceptor implements AcceptsValidator<AcceptsWithAnnotations> {

	private List<Class<? extends Annotation>> allowedTypes;

	@Override
	public boolean validate(ControllerMethod controllerMethod, ControllerInstance instance) {
		return containsAllowedTypes(instance.getBeanClass().getAnnotations()) 
				|| containsAllowedTypes(controllerMethod.getAnnotations());
	}
	
	private boolean containsAllowedTypes(Annotation[] annotations) {
		Collection<Class<? extends Annotation>> currentTypes = transform(asList(annotations), new AnnotationInstanceToType());
		return !Collections.disjoint(allowedTypes, currentTypes);
	}

	@Override
	public void initialize(AcceptsWithAnnotations annotation) {
		this.allowedTypes = asList(annotation.value());
	}

	private class AnnotationInstanceToType implements Function<Annotation, Class<? extends Annotation>> {

		@Override
		public Class<? extends Annotation> apply(Annotation input) {
			return input.annotationType();
		}
	}
}
