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
package br.com.caelum.vraptor;

 import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.StaticContentHandler;
import br.com.caelum.vraptor.ioc.Container;

public class VRaptorTest {

	private @Mock FilterConfig config;
	private @Mock ServletContext context;
	private @Mock static Container container;
	private @Mock InterceptorStack stack;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = ServletException.class)
	public void shoudlComplainIfNotInAServletEnviroment() throws IOException, ServletException {
		ServletRequest request = mock(ServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		
		new VRaptor().doFilter(request, response, null);
	}

	@Test
	@Ignore
	public void shouldDeferToContainerIfStaticFile() throws IOException, ServletException {
		VRaptor vraptor = new VRaptor();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		StaticContentHandler handler = mock(StaticContentHandler.class);
		FilterChain chain = mock(FilterChain.class);
		
		when(handler.requestingStaticFile(request)).thenReturn(true);
		
		vraptor.doFilter(request, response, chain);
		
		verify(handler, times(1)).deferProcessingToContainer(chain, request, response);
	}
}
