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
package br.com.caelum.vraptor4.core;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import br.com.caelum.vraptor4.VRaptorException;
import br.com.caelum.vraptor4.interceptor.Interceptor;
import br.com.caelum.vraptor4.interceptor.InterceptorRegistry;

/**
 * Delegates ordering to {@link InterceptorRegistry}
 * @author Lucas Cavalcanti
 * @since 3.3.0
 *
 */
@Dependent
@Default
public class EnhancedRequestExecution implements RequestExecution {

	private final InterceptorRegistry registry;
	private final InterceptorStack stack;

	@Inject
	public EnhancedRequestExecution(InterceptorStack stack, InterceptorRegistry registry) {
		this.stack = stack;
		this.registry = registry;
	}

	public void execute() throws VRaptorException {
		for (Class<? extends Interceptor> interceptor : registry.all()) {
			stack.add(interceptor);
		}
		stack.next(null, null);
	}

}
