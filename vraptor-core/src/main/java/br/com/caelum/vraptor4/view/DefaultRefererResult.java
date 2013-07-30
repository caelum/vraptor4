/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor4.view;

import static br.com.caelum.vraptor4.view.Results.logic;
import static br.com.caelum.vraptor4.view.Results.page;

import java.util.ArrayList;

import javax.inject.Inject;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor4.Result;
import br.com.caelum.vraptor4.core.Localization;
import br.com.caelum.vraptor4.http.MutableRequest;
import br.com.caelum.vraptor4.http.ParametersProvider;
import br.com.caelum.vraptor4.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor4.http.route.ControllerNotFoundException;
import br.com.caelum.vraptor4.http.route.Router;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4.restfulie.controller.HttpMethod;
import br.com.caelum.vraptor4.validator.Message;

public class DefaultRefererResult implements RefererResult {

	private final MutableRequest request;
	private final Result result;
	private final Router router;
	private final ParametersProvider provider;
	private final Localization localization;

	@Inject
	public DefaultRefererResult(Result result, MutableRequest request, Router router,
				ParametersProvider provider, Localization localization) {
		this.result = result;
		this.request = request;
		this.router = router;
		this.provider = provider;
		this.localization = localization;
	}

	public void forward() throws IllegalStateException {
		String referer = getReferer();

		try {
			ControllerMethod method = router.parse(referer, HttpMethod.GET, request);
			executeMethod(method, result.use(logic()).forwardTo(method.getController().getType()));
		} catch (ControllerNotFoundException e) {
			result.use(page()).forwardTo(referer);
		} catch (MethodNotAllowedException e) {
			result.use(page()).forwardTo(referer);
		}
	}

	private void executeMethod(ControllerMethod method, Object instance) {
		new Mirror().on(instance).invoke().method(method.getMethod())
			.withArgs(provider.getParametersFor(method, new ArrayList<Message>(), localization.getBundle()));
	}

	public void redirect() throws IllegalStateException {
		String referer = getReferer();
		try {
			ControllerMethod method = router.parse(referer, HttpMethod.GET, request);
			executeMethod(method, result.use(logic()).redirectTo(method.getController().getType()));
		} catch (ControllerNotFoundException e) {
			result.use(page()).redirectTo(referer);
		} catch (MethodNotAllowedException e) {
			result.use(page()).redirectTo(referer);
		}
	}

	private String getReferer() {
		String referer = request.getHeader("Referer");
		if (referer == null) {
			throw new IllegalStateException("The Referer header was not specified");
		}

		String path = request.getContextPath();
		return referer.substring(referer.indexOf(path) + path.length());
	}

}
