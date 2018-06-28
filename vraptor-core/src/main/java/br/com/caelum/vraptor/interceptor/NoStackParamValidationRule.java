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
import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.core.InterceptorStack;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Dependent
public class NoStackParamValidationRule implements ValidationRule {

	private final StepInvoker invoker;
	
	/** 
	 * @deprecated CDI eyes only 
	 */
	protected NoStackParamValidationRule() {
		this(null);
	}

	@Inject
	public NoStackParamValidationRule(StepInvoker invoker) {
		this.invoker = invoker;
	}

	@Override
	public void validate(Class<?> originalType, List<Method> methods) {

		Method aroundCall = invoker.findMethod(methods, AroundCall.class, originalType);
		Method afterCall = invoker.findMethod(methods, AfterCall.class, originalType);
		Method beforeCall = invoker.findMethod(methods, BeforeCall.class, originalType);
		Method accepts = invoker.findMethod(methods, Accepts.class, originalType);

		String interceptorStack = InterceptorStack.class.getName();
		String simpleInterceptorStack = SimpleInterceptorStack.class.getName();

		checkState(aroundCall == null || containsStack(aroundCall), "@AroundCall method must receive %s or %s",
				interceptorStack, simpleInterceptorStack);

		checkState(!containsStack(beforeCall) && !containsStack(afterCall) && !containsStack(accepts),
				"Non @AroundCall method must not receive %s or %s", interceptorStack, simpleInterceptorStack);
	}

	private boolean containsStack(Method method) {
		if (method == null) {
			return false;
		}

		List<Class<?>> parameterTypes = asList(method.getParameterTypes());
		return FluentIterable.from(parameterTypes).anyMatch(hasStack());
	}

	private Predicate<Class<?>> hasStack() {
		return new Predicate<Class<?>>() {
			@Override
			public boolean apply(Class<?> input) {
				return SimpleInterceptorStack.class.isAssignableFrom(input)
					|| InterceptorStack.class.isAssignableFrom(input);
			}
		};
	}
}
