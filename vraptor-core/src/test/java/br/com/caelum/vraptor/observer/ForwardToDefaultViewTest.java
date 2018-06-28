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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.RequestSucceded;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.view.MockedPage;
import br.com.caelum.vraptor.view.PageResult;

@RunWith(MockitoJUnitRunner.class)
public class ForwardToDefaultViewTest {

	private ForwardToDefaultView interceptor;
	private @Mock Result result;
	private @Mock ControllerMethod method;
	private @Mock MethodInfo methodInfo;
	private @Mock MutableRequest request;
	private @Mock MutableResponse response;

	@Before
	public void setup() {
		this.interceptor = new ForwardToDefaultView(result);
	}

	@Test
	public void doesNothingIfResultWasAlreadyUsed() {
		when(result.used()).thenReturn(true);
		interceptor.forward(new RequestSucceded(request, response));
		verify(result, never()).use(PageResult.class);
	}
	
	@Test
	public void doesNothingIfResponseIsCommited() {
		when(response.isCommitted()).thenReturn(true);
		interceptor.forward(new RequestSucceded(request, response));
		verify(result, never()).use(PageResult.class);
	}

	@Test
	public void shouldForwardToViewWhenResultWasNotUsed() {
		when(result.used()).thenReturn(false);
		when(result.use(PageResult.class)).thenReturn(new MockedPage());
		interceptor.forward(new RequestSucceded(request, response));
		verify(result).use(PageResult.class);
	}
}
