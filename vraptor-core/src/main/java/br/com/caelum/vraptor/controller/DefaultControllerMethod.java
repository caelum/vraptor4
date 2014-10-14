/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.controller;

import javax.enterprise.inject.Vetoed;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

import static br.com.caelum.vraptor.proxy.CDIProxies.extractRawTypeIfPossible;

@Vetoed
public class DefaultControllerMethod implements ControllerMethod {

	private final BeanClass controller;
	private final Method method;

	public DefaultControllerMethod(BeanClass controller, Method method) {
		this.controller = controller;
		this.method = method;
	}

	public static ControllerMethod instanceFor(Class<?> type, Method method) {
		type = extractRawTypeIfPossible(type);
		return new DefaultControllerMethod(new DefaultBeanClass(type), method);
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public int getArity() {
		return method.getParameterTypes().length;
	}

	@Override
	public BeanClass getController() {
		return controller;
	}

	@Override
	public boolean containsAnnotation(Class<? extends Annotation> annotation) {
		return method.isAnnotationPresent(annotation);
	}

	@Override
	public Annotation[] getAnnotations() {
		return method.getAnnotations();
	}

	@Override
	public String toString() {
		return "[DefaultControllerMethod: " + method + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(method, controller);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		DefaultControllerMethod other = (DefaultControllerMethod) obj;
		return Objects.equals(method, other.method) && Objects.equals(controller, other.controller);
	}

}
