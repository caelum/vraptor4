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
package br.com.caelum.vraptor.observer;

import static br.com.caelum.vraptor.controller.HttpMethod.POST;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.ControllerNotFoundHandler;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.events.ControllerMethodDiscovered;
import br.com.caelum.vraptor.events.NewRequest;
import br.com.caelum.vraptor.events.StackStarting;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.http.UrlToControllerTranslator;
import br.com.caelum.vraptor.http.route.ControllerNotFoundException;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;

public class RequestHandlerObserverTest {

	private @Mock UrlToControllerTranslator translator;
	private @Mock MutableRequest webRequest;
	private @Mock MutableResponse webResponse;
	private @Mock RequestInfo info;
	private RequestHandlerObserver observer;
	private @Mock MethodInfo methodInfo;
	private @Mock ControllerNotFoundHandler notFoundHandler;
	private @Mock MethodNotAllowedHandler methodNotAllowedHandler;
	private @Mock Event<ControllerMethodDiscovered> event;
	private @Mock InterceptorStack interceptorStack;
	private @Mock Event<StackStarting> stackStartingEvent;

	@Before
	public void config() {
		MockitoAnnotations.initMocks(this);
		info = new RequestInfo(null, null, webRequest, webResponse);
		observer = new RequestHandlerObserver(translator, notFoundHandler, methodNotAllowedHandler, event, interceptorStack, stackStartingEvent);
	}

	@Test
	public void shouldHandle404() throws Exception {
		when(translator.translate(info)).thenThrow(new ControllerNotFoundException());
		observer.handle(new NewRequest(), methodInfo, info);
		verify(notFoundHandler).couldntFind(info);
		verify(interceptorStack, never()).start();
	}

	@Test
	public void shouldHandle405() throws Exception {
		EnumSet<HttpMethod> allowedMethods = EnumSet.of(HttpMethod.GET);
		when(translator.translate(info)).thenThrow(new MethodNotAllowedException(allowedMethods, POST.toString()));
		observer.handle(new NewRequest(), methodInfo, info);
		verify(methodNotAllowedHandler).deny(info, allowedMethods);
		verify(interceptorStack, never()).start();
	}

	@Test
	public void shouldUseControllerMethodFoundWithNextInterceptor() throws Exception {
		final ControllerMethod method = mock(ControllerMethod.class);
		when(translator.translate(info)).thenReturn(method);
		observer.handle(new NewRequest(), methodInfo, info);
		verify(methodInfo).setControllerMethod(method);
		verify(interceptorStack).start();
	}
}