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

package br.com.caelum.vraptor.observer;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.vidageek.mirror.exception.ReflectionProviderException;

import org.slf4j.Logger;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.events.InterceptorsExecuted;
import br.com.caelum.vraptor.events.MethodExecuted;
import br.com.caelum.vraptor.events.MethodReady;
import br.com.caelum.vraptor.interceptor.ApplicationLogicException;
import br.com.caelum.vraptor.validator.Messages;
import br.com.caelum.vraptor.validator.ValidationException;

/**
 * Interceptor that executes the logic method.
 *
 * @author Guilherme Silveira
 * @author Rodrigo Turini
 * @author Victor Harada
 */
@Dependent
public class ExecuteMethod {

	private final static Logger log = getLogger(ExecuteMethod.class);

	private final MethodInfo methodInfo;
	private final Messages messages;
	private final ReflectionProvider reflectionProvider;

	private final Event<MethodExecuted> methodExecutedEvent;
	private final Event<MethodReady> methodReady;

	@Inject
	public ExecuteMethod(MethodInfo methodInfo, Messages messages, Event<MethodExecuted> methodExecutedEvent, 
			Event<MethodReady> methodReady, ReflectionProvider reflectionProvider) {
		this.methodInfo = methodInfo;
		this.messages = messages;
		this.methodExecutedEvent = methodExecutedEvent;
		this.methodReady = methodReady;
		this.reflectionProvider = reflectionProvider;
	}

	public void execute(@Observes InterceptorsExecuted event) {
		try {
			ControllerMethod method = event.getControllerMethod();
			methodReady.fire(new MethodReady(method));
			Method reflectionMethod = method .getMethod();
			Object[] parameters = methodInfo.getParametersValues();

			log.debug("Invoking {}", reflectionMethod);
			Object instance = event.getControllerInstance();
			Object result = reflectionProvider.invoke(instance, reflectionMethod, parameters);

			messages.assertAbsenceOfErrors();
			
			this.methodInfo.setResult(result);
			methodExecutedEvent.fire(new MethodExecuted(method, methodInfo));
		} catch (IllegalArgumentException e) {
			throw new InterceptionException(e);
		} catch (ReflectionProviderException e) {
			throwIfNotValidationException(e, e.getCause());
		} catch (Exception e) {
			throwIfNotValidationException(e, e);
		}
	}

	private void throwIfNotValidationException(Throwable original, Throwable alternativeCause) {
		Throwable cause = original.getCause();

		if (original instanceof ValidationException || cause instanceof ValidationException) {
			// fine... already parsed
			log.trace("swallowing {}", cause);
		} else {
			throw new ApplicationLogicException(alternativeCause);
		}
	}
}