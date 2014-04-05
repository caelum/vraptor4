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
package br.com.caelum.vraptor.ioc;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.events.RequestStarted;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.http.VRaptorResponse;

public class AnotherFrameworkRequestStarted implements RequestStarted {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private FilterChain chain;

	public AnotherFrameworkRequestStarted(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) {

		this.request = request;
		this.response = response;
		this.chain = chain;
	}

	@Override
	public FilterChain getChain() {
		return chain;
	}

	@Override
	public MutableRequest getRequest() {
		return new VRaptorRequest(request);
	}

	@Override
	public MutableResponse getResponse() {
		return new VRaptorResponse(response);
	}
}
