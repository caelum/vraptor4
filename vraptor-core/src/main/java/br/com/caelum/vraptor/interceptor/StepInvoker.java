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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor.InterceptionException;

import com.google.common.base.Throwables;

@ApplicationScoped
public class StepInvoker {

	private class InvokeMatcher implements Matcher<Method> {

		private Class<? extends Annotation> step;

		public InvokeMatcher(Class<? extends Annotation> step) {
			this.step = step;
		}

		@Override
		public boolean accepts(Method element) {
			if(element.getDeclaringClass().getSimpleName().contains("$")) {
				return false;
			}
			return element.isAnnotationPresent(this.step);
		}
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
			return new Mirror().on(interceptor).invoke().method(stepMethod).withArgs(params);
		} catch (Exception e) {
			// we dont wanna wrap it if it is a simple controller business logic
			// exception
			Throwables.propagateIfInstanceOf(e.getCause(), ApplicationLogicException.class);
			throw new InterceptionException(e.getCause());
		}
	}

	private boolean isNotSameClass(MirrorList<Method> methods, Class<?> interceptorClass) {
		for (Method possibleMethod : methods) {
			if (!possibleMethod.getDeclaringClass().equals(interceptorClass)) {
				return false;
			}
		}
		return true;
	}

	public MirrorList<Method> findAllMethods(Class<?> interceptorClass) {
		return new Mirror().on(interceptorClass).reflectAll().methods();
	}

	public Method findMethod(MirrorList<Method> interceptorMethods, Class<? extends Annotation> step,
			Class<?> interceptorClass) {

		MirrorList<Method> possibleMethods = interceptorMethods.matching(new InvokeMatcher(step));
		if (possibleMethods.size() > 1 && isNotSameClass(possibleMethods, interceptorClass)) {
			throw new IllegalStateException(interceptorClass.getCanonicalName() + " - You should not "
					+ "have more than one @" + step.getSimpleName() + " annotated method");
		}
		return possibleMethods.isEmpty() ? null : possibleMethods.get(0);
	}
}