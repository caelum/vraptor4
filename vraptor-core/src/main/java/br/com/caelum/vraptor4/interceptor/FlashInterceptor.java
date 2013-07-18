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
package br.com.caelum.vraptor4.interceptor;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.Result;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.http.MutableResponse;
import br.com.caelum.vraptor4.http.MutableResponse.RedirectListener;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;

/**
 * Interceptor that handles flash scope.
 * @author Lucas Cavalcanti
 * @author Adriano Almeida
 * @since 3.0.2
 */
@Intercepts
public class FlashInterceptor implements Interceptor {

	final static String FLASH_INCLUDED_PARAMETERS = "br.com.caelum.vraptor4.flash.parameters";
	private static final Logger LOGGER = LoggerFactory.getLogger(FlashInterceptor.class);

	private HttpSession session;
	private Result result;
	private MutableResponse response;

	//CDI eyes only
	@Deprecated
	public FlashInterceptor() {
	}

	@Inject
	public FlashInterceptor(HttpSession session, Result result, MutableResponse response) {
		this.session = session;
		this.result = result;
		this.response = response;
	}

	@Override
	public boolean accepts(ControllerMethod method) {
		return true;
	}

	@Override
	public void intercept(InterceptorStack stack, ControllerMethod method, Object resourceInstance)
			throws InterceptionException {
		@SuppressWarnings("unchecked")
		Map<String, Object> parameters = (Map<String, Object>) session.getAttribute(FLASH_INCLUDED_PARAMETERS);
		if (parameters != null) {
			session.removeAttribute(FLASH_INCLUDED_PARAMETERS);
			for (Entry<String, Object> parameter : parameters.entrySet()) {
				result.include(parameter.getKey(), parameter.getValue());
			}
		}
		response.addRedirectListener(new RedirectListener() {
			@Override
			public void beforeRedirect() {
				Map<String, Object> included = result.included();
				if (!included.isEmpty()) {
					try {
						session.setAttribute(FLASH_INCLUDED_PARAMETERS, included);
					} catch (IllegalStateException e) {
						LOGGER.info("HTTP Session was invalidated. It is not possible to include " +
								"Result parameters on Flash Scope");
					}
				}
			}
		});
		stack.next(method, resourceInstance);

	}


}
