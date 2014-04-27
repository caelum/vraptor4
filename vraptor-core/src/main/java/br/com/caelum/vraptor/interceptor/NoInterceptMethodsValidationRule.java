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

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.InterceptionException;

@Dependent
public class NoInterceptMethodsValidationRule implements ValidationRule {

	private StepInvoker stepInvoker;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected NoInterceptMethodsValidationRule() {
	}

	@Inject
	public NoInterceptMethodsValidationRule(StepInvoker stepInvoker) {
		this.stepInvoker = stepInvoker;
	}

	@Override
	public void validate(Class<?> originalType, MirrorList<Method> methods) {

		boolean hasAfterMethod = hasAnnotatedMethod(AfterCall.class, originalType, methods);
		boolean hasAroundMethod = hasAnnotatedMethod(AroundCall.class, originalType, methods);
		boolean hasBeforeMethod = hasAnnotatedMethod(BeforeCall.class, originalType, methods);

		if (!hasAfterMethod && !hasAroundMethod && !hasBeforeMethod) {
			throw new InterceptionException(format("Interceptor %s must "
				+ "declare at least one method whith @AfterCall, @AroundCall "
				+ "or @BeforeCall annotation", originalType.getCanonicalName()));
		}
	}

	private boolean hasAnnotatedMethod(Class<? extends Annotation> step,
			Class<?> originalType, MirrorList<Method> methods) {
		return stepInvoker.findMethod(methods, step, originalType) != null;
	}
}