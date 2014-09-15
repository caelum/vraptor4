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
package br.com.caelum.vraptor.musicjungle.interceptor;

import static java.util.Arrays.asList;

import java.util.ResourceBundle;

import javax.inject.Inject;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor.musicjungle.controller.HomeController;
import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.validator.I18nMessage;

/**
 * Interceptor to check if the user is in the session.
 */
@Intercepts
public class AuthorizationInterceptor {

	private final UserInfo info;
	private final UserDao dao;
	private final Result result;
	private final ResourceBundle bundle;

	@Inject
	public AuthorizationInterceptor(UserInfo info, UserDao dao, 
			Result result, ResourceBundle bundle) {
		this.info = info;
		this.dao = dao;
		this.result = result;
		this.bundle = bundle;
	}
	
	/**
	 * @deprecated CDI eyes only
	 */
	public AuthorizationInterceptor() {
		this(null, null, null, null);
	}

	@Accepts
	public boolean accepts(ControllerMethod method) {
		return !method.containsAnnotation(Public.class);
	}

	/**
	 * Intercepts the request and checks if there is a user logged in.
	 */
	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {

		User current = info.getUser();
		try {
			dao.refresh(current);
		} catch (Exception e) {
			// could happen if the user does not exist in the database or if there's no user logged in.
		}

		/**
		 * You can use the result even in interceptors, but you can't use Validator.onError* methods because
		 * they throw ValidationException.
		 */
		if (current == null) {
			// remember added parameters will survive one more request, when there is a redirect
			I18nMessage msg = new I18nMessage("user", "not_logged_user");
			msg.setBundle(bundle);
			result.include("errors", asList(msg));
			result.redirectTo(HomeController.class).login();
			return;
		}
		stack.next();
	}

}
