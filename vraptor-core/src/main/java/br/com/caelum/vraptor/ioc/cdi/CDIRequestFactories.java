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
package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.interceptor.Interceptor;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.events.RequestStarted;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

@RequestScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CDIRequestFactories {

	private RequestStarted requestStarted;

	public void setRequest(RequestStarted requestStarted) {
		this.requestStarted = requestStarted;
	}

	@Produces
	@RequestScoped
	public HttpSession getSession(){
		return requestStarted.getRequest().getSession();
	}

	@Produces
	@RequestScoped
	public MutableResponse getResponse(){
		return requestStarted.getResponse();
	}

	@Produces
	@RequestScoped
	public MutableRequest getRequest(){
		return requestStarted.getRequest();
	}

	@Produces
	@RequestScoped
	public FilterChain getChain(){
		return requestStarted.getChain();
	}
}
