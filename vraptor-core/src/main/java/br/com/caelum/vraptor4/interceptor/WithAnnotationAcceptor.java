package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Verify if certain annotations are presents in class or method.
 * 
 * @author Alberto Souza
 * 
 */
public class WithAnnotationAcceptor implements
		AcceptsValidator<AcceptsWithAnnotations> {

	private List<Class<? extends Annotation>> allowedTypes;

	@Override
	public boolean validate(ControllerMethod controllerMethod,
			ControllerInstance instance) {
		Collection<Class<? extends Annotation>> currentTypes = Collections2
				.transform(Arrays.asList(instance.getBeanClass().getAnnotations()),
						new AnnotationInstanceToType());
		if (!Collections.disjoint(allowedTypes, currentTypes)) {
			return true;
		}
		currentTypes = Collections2
				.transform(Arrays.asList(controllerMethod.getAnnotations()),
						new AnnotationInstanceToType());
		if (!Collections.disjoint(allowedTypes, currentTypes)) {
			return true;
		}

		return false;
	}

	@Override
	public void initialize(AcceptsWithAnnotations annotation) {
		this.allowedTypes = Arrays.asList(annotation.value());
	}

	private class AnnotationInstanceToType implements
			Function<Annotation, Class<? extends Annotation>> {

		@Override
		public Class<? extends Annotation> apply(Annotation input) {
			return input.annotationType();
		}

	}
}
