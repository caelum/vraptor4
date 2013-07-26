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

import java.util.Arrays;

import javax.inject.Inject;

import br.com.caelum.vraptor.musicjungle.controller.HomeController;
import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.Result;
import br.com.caelum.vraptor4.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4.validator.ValidationMessage;

/**
 * Interceptor to check if the user is in the session.
 */
@Intercepts
public class AuthorizationInterceptor{


	@Inject
	private UserInfo info;
	
	@Inject
	private UserDao dao;
	
	@Inject
	private Result result;
	
	@Accepts
	public boolean accepts(ControllerMethod method){
		return !method.containsAnnotation(Public.class);
	}

    /**
     * Intercepts the request and checks if there is a user logged in.
     */
	@AroundCall
    public void intercept(SimpleInterceptorStack stack)
            throws InterceptionException {
    	/**
    	 * You can use the result even in interceptors.
    	 */
    	if (info.getUser() == null) {
    		// remember added parameters will survive one more request, when there is a redirect
    		result.include("errors", Arrays.asList(new ValidationMessage("user is not logged in", "user")));
    		result.redirectTo(HomeController.class).login();
    	} else {
	    	dao.refresh(info.getUser());
	    	// continues execution
	    	stack.next();
    	}
    }

}
