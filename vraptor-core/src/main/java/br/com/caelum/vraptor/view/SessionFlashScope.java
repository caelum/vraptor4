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
package br.com.caelum.vraptor.view;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.controller.ControllerMethod;
/**
 * FlashScope that uses the HttpSession to hold the data
 *
 * @author Lucas Cavalcanti
 * @since 3.3.0
 */
@RequestScoped
public class SessionFlashScope implements FlashScope {

	private final HttpSession session;

	private static final String KEY_START = "vraptor_flash_parameters_for_";

	/** 
	 * @deprecated CDI eyes only
	 */
	protected SessionFlashScope() {
		this(null);
	}

	@Inject
	public SessionFlashScope(HttpSession session) {
		this.session = session;
	}

	@Override
	public Object[] consumeParameters(ControllerMethod method) {
		Object[] args = (Object[]) session.getAttribute(nameFor(method));
		if (args != null) {
			session.removeAttribute(nameFor(method));
		}
		return args;
	}

	private String nameFor(ControllerMethod method) {
		return KEY_START + method.getMethod();
	}

	@Override
	public void includeParameters(ControllerMethod method, Object[] args) {
		session.setAttribute(nameFor(method), args);
	}

}
