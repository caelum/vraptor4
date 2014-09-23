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
package br.com.caelum.vraptor.controller;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

public class DefaultMethodNotAllowedHandlerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private DefaultMethodNotAllowedHandler handler;
	private MutableResponse response;
	private MutableRequest request;

	@Before
	public void setUp() throws Exception {
		this.response = mock(MutableResponse.class);
		this.request = mock(MutableRequest.class);
		this.handler = new DefaultMethodNotAllowedHandler();
	}

	@Test
	public void shouldAddAllowHeader() throws Exception {
		this.handler.deny(request, response, EnumSet.of(HttpMethod.GET, HttpMethod.POST));

		verify(response).addHeader("Allow", "GET, POST");

	}

	@Test
	public void shouldSendErrorMethodNotAllowed() throws Exception {
		this.handler.deny(request, response, EnumSet.of(HttpMethod.GET, HttpMethod.POST));

		verify(response).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
	@Test
	public void shouldNotSendMethodNotAllowedIfTheRequestMethodIsOptions() throws Exception {
		when(request.getMethod()).thenReturn("OPTIONS");

		this.handler.deny(request, response, EnumSet.of(HttpMethod.GET, HttpMethod.POST));

		verify(response, never()).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
	@Test
	public void shouldThrowInterceptionExceptionIfAnIOExceptionOccurs() throws Exception {
		exception.expect(InterceptionException.class);

		doThrow(new IOException()).when(response).sendError(anyInt());
		this.handler.deny(request, response, EnumSet.of(HttpMethod.GET, HttpMethod.POST));
	}
}
