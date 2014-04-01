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

package br.com.caelum.vraptor.ioc;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.events.RequestStarted;
import br.com.caelum.vraptor.events.VRaptorRequestStarted;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.http.VRaptorResponse;

/**
 * Simple factory for request started event production.
 *
 * @author Erich Egert
 */
@ApplicationScoped
public class RequestStartedFactory {

	public RequestStarted createEvent(HttpServletRequest baseRequest,
			HttpServletResponse baseResponse, FilterChain chain) {

		VRaptorRequest mutableRequest = new VRaptorRequest(baseRequest);
		VRaptorResponse mutableResponse = new VRaptorResponse(baseResponse);

		return new VRaptorRequestStarted(chain, mutableRequest, mutableResponse);
	}
}
