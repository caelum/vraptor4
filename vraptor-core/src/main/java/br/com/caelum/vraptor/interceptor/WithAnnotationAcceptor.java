package br.com.caelum.vraptor.interceptor;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.enterprise.context.Dependent;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Verify if certain annotations are presents in class or method.
 *
 * @author Alberto Souza
 *
 */
@Dependent
public class WithAnnotationAcceptor implements AcceptsValidator<AcceptsWithAnnotations> {

	private List<Class<? extends Annotation>> allowedTypes;

	@Override
	public boolean validate(ControllerMethod controllerMethod, ControllerInstance instance) {
		return containsAllowedTypes(instance.getBeanClass().getAnnotations()) 
				|| containsAllowedTypes(controllerMethod.getAnnotations());
	}

	private boolean containsAllowedTypes(Annotation[] annotations) {
		return FluentIterable.from(asList(annotations)).anyMatch(isAllowedType());
	}

	private Predicate<Annotation> isAllowedType() {
		return new Predicate<Annotation>() {
			@Override
			public boolean apply(Annotation input) {
				return allowedTypes.contains(input.annotationType());
			}
		};
	}

	@Override
	public void initialize(AcceptsWithAnnotations annotation) {
		this.allowedTypes = asList(annotation.value());
	}
}
