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

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.InterceptionException;

@Dependent
public class AcceptsNeedReturnBooleanValidationRule implements ValidationRule {

	private final StepInvoker invoker;

	/** 
	 * @deprecated CDI eyes only 
	 */
	protected AcceptsNeedReturnBooleanValidationRule() {
		this(null);
	}

	@Inject
	public AcceptsNeedReturnBooleanValidationRule(StepInvoker invoker) {
		this.invoker = invoker;
	}

	@Override
	public void validate(Class<?> originalType, List<Method> methods) {
		Method accepts = invoker.findMethod(methods, Accepts.class, originalType);

		if (accepts != null && !isBooleanReturn(accepts.getReturnType())) {
			throw new InterceptionException(format("@%s method must return boolean", 
					Accepts.class.getSimpleName()));
		}
	}

	private boolean isBooleanReturn(Class<?> returnType) {
		return returnType.equals(Boolean.class) || returnType.equals(boolean.class);
	}
}
