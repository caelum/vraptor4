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

package br.com.caelum.vraptor.interceptor;

import static br.com.caelum.vraptor.view.Results.nothing;

import java.lang.reflect.Method;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.MethodExecuted;
import br.com.caelum.vraptor.reflection.MethodExecutor;
import br.com.caelum.vraptor.reflection.MethodExecutorException;
import br.com.caelum.vraptor.util.Stringnifier;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.validator.Validator;

/**
 * Interceptor that executes the logic method.
 *
 * @author Guilherme Silveira
 */
@Intercepts(after = ParametersInstantiatorInterceptor.class, before = {})
public class ExecuteMethodInterceptor implements Interceptor {

	private final static Logger log = LoggerFactory.getLogger(ExecuteMethodInterceptor.class);

	private final MethodInfo info;
	private final Validator validator;
	private final MethodExecutor methodExecutor;

	private Event<MethodExecuted> methodExecutedEvent;

	/**
	 * @deprecated CDI eyes only
	 */
	protected ExecuteMethodInterceptor() {
		this(null, null, null, null);
	}

	@Inject
	public ExecuteMethodInterceptor(MethodInfo info, Validator validator, MethodExecutor methodExecutor,
			Event<MethodExecuted> methodExecutedEvent) {
		this.info = info;
		this.validator = validator;
		this.methodExecutor = methodExecutor;
		this.methodExecutedEvent = methodExecutedEvent;
	}

	@Override
	public void intercept(InterceptorStack stack, ControllerMethod method, Object controllerInstance)
			throws InterceptionException {
		try {
			Method reflectionMethod = method.getMethod();
			Object[] parameters = this.info.getParameters();

			log.debug("Invoking {}", Stringnifier.simpleNameFor(reflectionMethod));
			Object result = methodExecutor.invoke(reflectionMethod, controllerInstance, parameters);

			if (validator.hasErrors()) { // method should have thrown
											// ValidationException
				if (log.isDebugEnabled()) {
					try {
						validator.onErrorUse(nothing());
					} catch (ValidationException e) {
						log.debug("Some validation errors occured: {}", e.getErrors());
					}
				}
				throw new InterceptionException(
						"There are validation errors and you forgot to specify where to go. Please add in your method "
								+ "something like:\n"
								+ "validator.onErrorUse(page()).of(AnyController.class).anyMethod();\n"
								+ "or any view that you like.\n"
								+ "If you didn't add any validation error, it is possible that a conversion error had happened.");
			}
			this.info.setResult(result);
			methodExecutedEvent.fire(new MethodExecuted(method, info));
			stack.next(method, controllerInstance);
		} catch (IllegalArgumentException e) {
			throw new InterceptionException(e);
		} catch (MethodExecutorException e) {
			throwIfNotValidationException(e,
					new ApplicationLogicException("your controller raised an exception", e.getCause()));
		} catch (Exception e) {
			throwIfNotValidationException(e, new InterceptionException(e));
		}
	}

	private void throwIfNotValidationException(Throwable original, RuntimeException alternative) {
		Throwable cause = original.getCause();

		if (original instanceof ValidationException || cause instanceof ValidationException) {
			// fine... already parsed
			log.trace("swallowing {}", cause);
		} else {
			throw alternative;
		}
	}

	@Override
	public boolean accepts(ControllerMethod method) {
		return true;
	}

}
