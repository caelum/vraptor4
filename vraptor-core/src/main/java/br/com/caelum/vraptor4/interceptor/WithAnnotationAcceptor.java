package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Collections2;

import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

/**
 * Verify if certain annotations are presents in class or method.
 * @author Alberto Souza
 *
 */
public class WithAnnotationAcceptor implements AcceptsValidator<AcceptsWithAnnotations> {

	private List<Class<? extends Annotation>> allowedTypes;

	@Override
	public boolean validate(ControllerMethod controllerMethod, ControllerInstance instance) {
		List<Annotation> classAnnotations = Arrays.asList(instance.getController().getClass().getAnnotations());
		if(!Collections.disjoint(allowedTypes,classAnnotations)){
			return true;
		}
		List<Annotation> methodAnnotations = Arrays.asList(controllerMethod.getMethod().getAnnotations());
		if(!Collections.disjoint(allowedTypes,methodAnnotations)){
			return true;
		}
		
		return false;
	}

	@Override
	public void initialize(AcceptsWithAnnotations annotation) {
		this.allowedTypes = Arrays.asList(annotation.value());
	}

}
