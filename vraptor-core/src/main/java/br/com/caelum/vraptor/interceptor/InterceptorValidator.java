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

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.Intercepts;

@Dependent
public class InterceptorValidator {

	private @Inject @Any Instance<ValidationRule> validationRules;
	private @Inject @Any StepInvoker stepInvoker;

	public void validate(Class<?> originalType) {
		boolean implementsInterceptor = Interceptor.class.isAssignableFrom(originalType);
		boolean containsIntercepts = originalType.isAnnotationPresent(Intercepts.class);

		checkState(implementsInterceptor || containsIntercepts, 
				"Annotation @Intercepts found in %s, but it is not an Interceptor.", originalType);

		applyNewInterceptorValidationRules(originalType, implementsInterceptor);
	}

	private void applyNewInterceptorValidationRules(Class<?> originalType,
			boolean implementsInterceptor) {

		if (!implementsInterceptor) {
			List<Method> allMethods = stepInvoker.findAllMethods(originalType);
			for (ValidationRule validationRule : this.validationRules) {
				validationRule.validate(originalType, allMethods);
			}
		}
	}
}
