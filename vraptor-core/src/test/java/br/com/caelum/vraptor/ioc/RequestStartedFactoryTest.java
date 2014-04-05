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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.MockStaticContentHandler;
import br.com.caelum.vraptor.VRaptor;
import br.com.caelum.vraptor.WeldJunitRunner;
import br.com.caelum.vraptor.ioc.cdi.Contexts;

@RunWith(WeldJunitRunner.class)
public class RequestStartedFactoryTest {

	@Inject private MockStaticContentHandler handler;
	@Inject private VRaptor vRaptor;
	@Inject private MockRequestHandlerObserver requestHandler;
	@Inject private Contexts contexts;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private FilterChain chain;

	@Before
	public void setup() {
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		chain = mock(FilterChain.class);
		handler.setRequestingStaticFile(false);
		contexts.startRequestScope();
	}

	@Test
	public void shouldNotRunVRaptorStackIfVRaptorRequestStartedEventNotFired() throws Exception {
		when(request.getRequestURI()).thenReturn(MockRequestStartedFactory.PATTERN_TO_AVOID_VRAPTOR_STACK);

		vRaptor.doFilter(request, response, chain);

		assertThat(requestHandler.isVraptorStackCalled(), is(false));
	}

	@Test
	public void shouldRunVRaptorStackIfVRaptorRequestStartedEventIsFired() throws Exception {
		when(request.getRequestURI()).thenReturn("someUrlThatMustBeInterceptedByVRaptor");

		vRaptor.doFilter(request, response, chain);

		assertThat(requestHandler.isVraptorStackCalled(), is(true));
	}

}
