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

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.inject.Vetoed;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;

@Vetoed
public class AspectStyleInterceptorHandler implements InterceptorHandler {

	private static final Logger logger = getLogger(AspectStyleInterceptorHandler.class);

	private final StepInvoker stepInvoker;
	private final Container container;
	private final Class<?> interceptorClass;
	private final CustomAcceptsExecutor customAcceptsExecutor;
	private final InterceptorAcceptsExecutor acceptsExecutor;
	private final InterceptorExecutor interceptorExecutor;
	private Method afterMethod;
	private Method aroundMethod;
	private Method beforeMethod;
	private Method acceptsMethod;
	private Method customAcceptsMethod;

	public AspectStyleInterceptorHandler(Class<?> interceptorClass, StepInvoker stepInvoker,
			Container container, CustomAcceptsExecutor customAcceptsExecutor,
			InterceptorAcceptsExecutor acceptsExecutor, InterceptorExecutor interceptorExecutor) {

		this.interceptorClass = interceptorClass;
		this.stepInvoker = stepInvoker;
		this.container = container;
		this.customAcceptsExecutor = customAcceptsExecutor;
		this.acceptsExecutor = acceptsExecutor;
		this.interceptorExecutor = interceptorExecutor;
		extractAllInterceptorMethods();
	}

	private void extractAllInterceptorMethods() {
		List<Method> methods = stepInvoker.findAllMethods(interceptorClass);
		this.afterMethod = findMethodWith(AfterCall.class, methods);
		this.aroundMethod = findMethodWith(AroundCall.class, methods);
		this.beforeMethod = findMethodWith(BeforeCall.class, methods);
		this.acceptsMethod = findMethodWith(Accepts.class, methods);
		this.customAcceptsMethod = findMethodWith(CustomAcceptsFailCallback.class, methods);
	}

	@Override
	public void execute(InterceptorStack stack, ControllerMethod controllerMethod, Object currentController) {

		Object interceptor = container.instanceFor(interceptorClass);
		logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());
		List<Annotation> customAccepts = customAcceptsExecutor.getCustomAccepts(interceptor);

		if (customAccepts(interceptor, customAccepts) || internalAccepts(interceptor, customAccepts)) {
			interceptorExecutor.execute(interceptor, beforeMethod);
			interceptorExecutor.executeAround(interceptor, aroundMethod);
			interceptorExecutor.execute(interceptor, afterMethod);
		} else {
			stack.next(controllerMethod, currentController);
		}
	}

	private Method findMethodWith(Class<? extends Annotation> step, List<Method> methods) {
		return stepInvoker.findMethod(methods, step, interceptorClass);
	}

	private boolean internalAccepts(Object interceptor, List<Annotation> customAccepts) {
		if (!customAccepts.isEmpty()) {
			return false;
		}

		return acceptsExecutor.accepts(interceptor, acceptsMethod);
	}

	private boolean customAccepts(Object interceptor, List<Annotation> customAccepts) {
		return customAcceptsExecutor.accepts(interceptor, customAcceptsMethod, customAccepts);
	}

	@Override
	public String toString() {
		return "AspectStyleInterceptorHandler for " + interceptorClass.getName();
	}
}
