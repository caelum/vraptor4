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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerNotFoundHandler;
import br.com.caelum.vraptor.controller.InvalidInputHandler;
import br.com.caelum.vraptor.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.events.ControllerFound;
import br.com.caelum.vraptor.events.RequestSucceded;
import br.com.caelum.vraptor.events.VRaptorRequestStarted;
import br.com.caelum.vraptor.http.UrlToControllerTranslator;
import br.com.caelum.vraptor.observer.RequestHandlerObserver;

@Specializes @ApplicationScoped
class MockRequestHandlerObserver extends RequestHandlerObserver{
	private boolean vraptorStackCalled;

	@SuppressWarnings("deprecation")
	public MockRequestHandlerObserver() {}

	@Inject
	public MockRequestHandlerObserver(UrlToControllerTranslator translator,
			ControllerNotFoundHandler controllerNotFoundHandler, MethodNotAllowedHandler methodNotAllowedHandler,
			Event<ControllerFound> controllerFoundEvent, Event<RequestSucceded> endRequestEvent,
			InterceptorStack interceptorStack, InvalidInputHandler invalidInputHandler) {
		super(translator, controllerNotFoundHandler, methodNotAllowedHandler, controllerFoundEvent, endRequestEvent, interceptorStack, invalidInputHandler);
	}

	public void handle(@Observes VRaptorRequestStarted event) {
		vraptorStackCalled = true;
	}

	public boolean isVraptorStackCalled() {
		return vraptorStackCalled;
	}
}
