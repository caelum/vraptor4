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

package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.observer.ExecuteMethodExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Vetoed;
import java.util.concurrent.Callable;

/**
 * Instantiates the interceptor on the fly and executes its method.
 *
 * @author Guilherme Silveira
 */
@Vetoed
public class ToInstantiateInterceptorHandler implements InterceptorHandler {

	private static final Logger logger = LoggerFactory.getLogger(ToInstantiateInterceptorHandler.class);

	private final Container container;
	private final Class<?> type;
	private final ExecuteMethodExceptionHandler executeMethodExceptionHandler;

	public ToInstantiateInterceptorHandler(Container container, Class<?> type, ExecuteMethodExceptionHandler executeMethodExceptionHandler) {
		this.container = container;
		this.type = type;
		this.executeMethodExceptionHandler = executeMethodExceptionHandler;
	}

	@Override
	public void execute(final InterceptorStack stack, final ControllerMethod method, final Object controllerInstance)
			throws InterceptionException {
		final Interceptor interceptor = (Interceptor) container.instanceFor(type);
		if (interceptor == null) {
			throw new InterceptionException("Unable to instantiate interceptor for " + type.getName()
					+ ": the container returned null.");
		}
		if (interceptor.accepts(method)) {
			logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());
			executeSafely(stack, method, controllerInstance, interceptor);
		} else {
			stack.next(method, controllerInstance);
		}
	}

	private void executeSafely(final InterceptorStack stack, final ControllerMethod method, final Object controllerInstance, final Interceptor interceptor) {
		Try result = Try.run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				interceptor.intercept(stack, method, controllerInstance);
				return null;
			}
		});
		if (result.failed()) {
			executeMethodExceptionHandler.handle(result.getException());
		}
	}

	@Override
	public String toString() {
		return "ToInstantiateHandler for " + type.getName();
	}
}
