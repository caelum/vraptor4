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

import static br.com.caelum.vraptor.interceptor.CustomAcceptsVerifier.getCustomAcceptsAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

@ApplicationScoped
public class CustomAcceptsExecutor {

	private final Instance<ControllerMethod> controllerMethod;
	private final Instance<ControllerInstance> controllerInstance;
	private final StepInvoker invoker;
	private final CustomAcceptsVerifier acceptsVerifier;

	/**
	 * @deprecated CDI eyes only
	 */
	protected CustomAcceptsExecutor() {
		this(null, null, null, null);
	}

	@Inject
	public CustomAcceptsExecutor(Instance<ControllerMethod> controllerMethod,
			Instance<ControllerInstance> controllerInstance,
			StepInvoker invoker, CustomAcceptsVerifier acceptsVerifier) {

		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
		this.invoker = invoker;
		this.acceptsVerifier = acceptsVerifier;
	}

	public boolean accepts(Object interceptor, Method method, List<Annotation> constraints) {
		if (constraints.isEmpty()) {
			return false;
		}

		boolean customAccepts = acceptsVerifier.isValid(interceptor,
				controllerMethod.get(), controllerInstance.get(), constraints);
		if (!customAccepts) {
			invoker.tryToInvoke(interceptor, method);
		}

		return customAccepts;
	}

	public List<Annotation> getCustomAccepts(Object interceptor) {
		return getCustomAcceptsAnnotations(interceptor.getClass());
	}
}
