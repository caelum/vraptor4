/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.interceptor;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.ioc.Container;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

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

	public static List<Annotation> getCustomAcceptsAnnotations(Class<?> clazz){
		return FluentIterable.from(asList(clazz.getAnnotations()))
			.filter(acceptsConstraintMatcher())
			.toList();
	}

	private static Predicate<Annotation> acceptsConstraintMatcher() {
		return new Predicate<Annotation>() {
			@Override
			public boolean apply(Annotation element) {
				return element != null && element.annotationType().isAnnotationPresent(AcceptsConstraint.class);
			}
		};
	}
}
