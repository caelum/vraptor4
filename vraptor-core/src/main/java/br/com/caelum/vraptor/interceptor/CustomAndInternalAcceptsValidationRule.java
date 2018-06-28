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
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import br.com.caelum.vraptor.Accepts;

@Dependent
public class CustomAndInternalAcceptsValidationRule implements ValidationRule {

	private final StepInvoker invoker;

	/**
	 * @deprecated CDI eyes only
	 */
	protected CustomAndInternalAcceptsValidationRule() {
		this(null);
	}

	@Inject
	public CustomAndInternalAcceptsValidationRule(StepInvoker invoker) {
		this.invoker = invoker;
	}

	@Override
	public void validate(Class<?> originalType, List<Method> methods) {

		Method accepts = invoker.findMethod(methods, Accepts.class, originalType);
		List<Annotation> constraints = getCustomAcceptsAnnotations(originalType);

		checkState(accepts == null || constraints.isEmpty(), 
				"Interceptor %s must declare internal accepts or custom, not both.", originalType);
	}
}
