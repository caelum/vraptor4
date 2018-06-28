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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
public class InterceptorMethodParametersResolver {

	private final Container container;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected InterceptorMethodParametersResolver() {
		this(null);
	}

	@Inject
	public InterceptorMethodParametersResolver(Container container) {
		this.container = container;
	}

	public Object[] parametersFor(Method methodToInvoke) {
		if (methodToInvoke == null)
			return new Object[] {};
		Class<?>[] parameterTypes = methodToInvoke.getParameterTypes();
		List<Object> parameters = new ArrayList<>();
		for (Class<?> parameterType : parameterTypes) {
			parameters.add(container.instanceFor(parameterType));
		}
		return parameters.toArray();
	}
}
