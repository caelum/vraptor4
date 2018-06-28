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

import static com.google.common.base.MoreObjects.firstNonNull;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InterceptorAcceptsExecutor {

	private final InterceptorMethodParametersResolver parameterResolver;
	private final StepInvoker invoker;

	/**
	 * @deprecated CDI eyes only
	 */
	protected InterceptorAcceptsExecutor() {
		this(null, null);
	}

	@Inject
	public InterceptorAcceptsExecutor(
			InterceptorMethodParametersResolver parameterResolver,
			StepInvoker invoker) {

		this.parameterResolver = parameterResolver;
		this.invoker = invoker;
	}

	public Boolean accepts(Object interceptor, Method method) {
		if(method != null) {
			Object[] params = parameterResolver.parametersFor(method);
			Object returnObject = invoker.tryToInvoke(interceptor, method, params);
			return firstNonNull((Boolean) returnObject, false);
		}
		return true;
	}
}
