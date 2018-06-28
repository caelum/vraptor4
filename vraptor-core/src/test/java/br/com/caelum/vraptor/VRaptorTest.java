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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.*;

import static org.hamcrest.Matchers.is;
import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(Arquillian.class)
public class VRaptorTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Inject
	private VRaptor vRaptor;

	@Inject
	private MockStaticContentHandler handler;

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap
			.create(WebArchive.class)
				.addPackages(true, "br.com.caelum.vraptor")
			.addAsManifestResource(INSTANCE, "beans.xml");
	}

	@Test
	public void shoudlComplainIfNotInAServletEnviroment() throws Exception {
		exception.expect(ServletException.class);

		ServletRequest request = mock(ServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		vRaptor.doFilter(request, response, null);
	}

	@Test
	public void shouldDeferToContainerIfStaticFile() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		handler.setRequestingStaticFile(true);
		vRaptor.doFilter(request, response, chain);
		assertThat(handler.isDeferProcessingToContainerCalled(), is(true));
	}

	@Test
	public void shouldBypassWebsocketRequests() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getHeader("Upgrade")).thenReturn("Websocket");

		vRaptor.doFilter(request, response, chain);
		verify(request).getHeader("Upgrade");
		verify(chain).doFilter(request, response);

		verifyNoMoreInteractions(request, response);
	}

}
