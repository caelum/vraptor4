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

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.propagateIfInstanceOf;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.ReflectionProvider;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@ApplicationScoped
public class StepInvoker {

	private final ReflectionProvider reflectionProvider;

	/**
	 * @deprecated CDI eyes only
	 */
	protected StepInvoker() {
		this(null);
	}

	@Inject
	public StepInvoker(ReflectionProvider reflectionProvider) {
		this.reflectionProvider = reflectionProvider;
	}

	public Object tryToInvoke(Object interceptor, Method stepMethod, Object... params) {
		if (stepMethod == null) {
			return null;
		}
		Object returnObject = invokeMethod(interceptor, stepMethod, params);
		if (stepMethod.getReturnType().equals(void.class)) {
			return new VoidReturn();
		}
		return returnObject;
	}

	private Object invokeMethod(Object interceptor, Method stepMethod, Object... params) {
		try {
			return reflectionProvider.invoke(interceptor, stepMethod, params);
		} catch (Exception e) {
			// we dont wanna wrap it if it is a simple controller business logic
			// exception
			propagateIfInstanceOf(e.getCause(), ApplicationLogicException.class);
			throw new InterceptionException(e.getCause());
		}
	}

	public List<Method> findAllMethods(Class<?> interceptorClass) {
		return reflectionProvider.getMethodsFor(interceptorClass);
	}

	public Method findMethod(List<Method> interceptorMethods, Class<? extends Annotation> step, Class<?> interceptorClass) {

		FluentIterable<Method> possibleMethods = FluentIterable.from(interceptorMethods).filter(hasStepAnnotation(step));

		if (possibleMethods.size() > 1 && possibleMethods.allMatch(not(notSameClass(interceptorClass)))) {
			throw new IllegalStateException(String.format("%s - You should not have more than one @%s annotated method",
					interceptorClass.getCanonicalName(), step.getSimpleName()));
		}

		return possibleMethods.first().orNull();
	}

	private Predicate<Method> notSameClass(final Class<?> interceptorClass) {
		return new Predicate<Method>() {
			@Override
			public boolean apply(Method possibleMethod) {
				return !possibleMethod.getDeclaringClass().equals(interceptorClass);
			}
		};
	}

	private Predicate<Method> hasStepAnnotation(final Class<? extends Annotation> step) {
		return new Predicate<Method>() {
			@Override
			public boolean apply(Method element) {
				if(element.getDeclaringClass().getSimpleName().contains("$")) {
					return false;
				}
				return element.isAnnotationPresent(step);
			}
		};
	}
}
