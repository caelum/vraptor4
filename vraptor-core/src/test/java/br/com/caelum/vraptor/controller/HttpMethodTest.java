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

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class HttpMethodTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private @Mock HttpServletRequest request;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldConvertGETStringToGetMethodForRequestParameter() throws Exception {
		when(request.getParameter("_method")).thenReturn("gEt");
		when(request.getMethod()).thenReturn("POST");

		assertEquals(HttpMethod.GET, HttpMethod.of(request));
	}

	@Test
	public void shouldThrowExceptionForNotKnowHttpMethodsForRequestParameter() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(containsString("HTTP Method not known"));

		when(request.getParameter("_method")).thenReturn("JUMP!");
		when(request.getMethod()).thenReturn("POST");
				
		HttpMethod.of(request);
	}

	@Test
	public void shouldThrowInvalidInputExceptionIf_methodIsUsedInGETRequests() throws Exception {
		exception.expect(InvalidInputException.class);

		when(request.getParameter("_method")).thenReturn("DELETE");
		when(request.getMethod()).thenReturn("GET");
		HttpMethod.of(request);
	}

	@Test
	public void shouldConvertGETStringToGetMethod() throws Exception {
		when(request.getParameter("_method")).thenReturn(null);
		when(request.getMethod()).thenReturn("gEt");
		
		assertEquals(HttpMethod.GET, HttpMethod.of(request));
	}

	@Test
	public void shouldThrowExceptionForNotKnowHttpMethods() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(containsString("HTTP Method not known"));

		when(request.getParameter("_method")).thenReturn(null);
		when(request.getMethod()).thenReturn("JUMP!");
		
		HttpMethod.of(request);
	}

	@Test
	public void shouldUseParameterNameBeforeTryingHttpRealMethod() throws Exception {
		when(request.getMethod()).thenReturn("dElEtE");
		when(request.getParameter("_method")).thenReturn("post");
		
		assertEquals(HttpMethod.POST, HttpMethod.of(request));
	}
}
