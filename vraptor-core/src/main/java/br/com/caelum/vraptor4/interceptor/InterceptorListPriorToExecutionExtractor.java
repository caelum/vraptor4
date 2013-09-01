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

package br.com.caelum.vraptor4.interceptor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.core.InterceptorStack;

import com.google.common.collect.Lists;

/**
 * Extracts all interceptors which are supposed to be applied for this current
 * controller method.
 *
 * @author Guilherme Silveira
 * @deprecated This class is deprecated. If you extend a request execution, consider using @Intercepts(after=..., before=...) instead.
 */
@Deprecated
@ApplicationScoped
public class InterceptorListPriorToExecutionExtractor implements Interceptor {


	private static final Logger logger = LoggerFactory.getLogger(InterceptorListPriorToExecutionExtractor.class);

    private final InterceptorRegistry registry;

    @Inject
    public InterceptorListPriorToExecutionExtractor(InterceptorRegistry registry) {
        this.registry = registry;
        logger.warn("This class is deprecated. If you extend a request execution, consider using @Intercepts(after=..., before=...) instead.");
    }

    public void intercept(InterceptorStack stack, ControllerMethod method, Object controllerInstance) throws InterceptionException {
    	for (Class<?> type : Lists.reverse(registry.all())) {
			stack.addAsNext(type);
		}
        stack.next(method, controllerInstance);
    }

    public boolean accepts(ControllerMethod method) {
        return true;
    }

}
