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
package br.com.caelum.vraptor.http;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.view.AcceptHeaderToFormat;

public class DefaultFormatResolverTest {

	private FormatResolver resolver;
	private @Mock HttpServletRequest request;
	private @Mock AcceptHeaderToFormat acceptHeaderToFormat;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		resolver = new DefaultFormatResolver(request, acceptHeaderToFormat);
	}

	@Test
	public void if_formatIsSpecifiedReturnIt() throws Exception {
		when(request.getParameter("_format")).thenReturn("xml");

		String format = resolver.getAcceptFormat();
		assertThat(format, is("xml"));
	}

	@Test
	public void if_formatIsSpecifiedReturnItEvenIfAcceptsHtml() throws Exception {
		when(request.getParameter("_format")).thenReturn("xml");
		when(request.getHeader("Accept")).thenReturn("html");

		String format = resolver.getAcceptFormat();
		assertThat(format, is("xml"));
	}

	@Test
	public void if_formatNotSpecifiedShouldReturnRequestAcceptFormat() {
		when(request.getParameter("_format")).thenReturn(null);
		when(request.getHeader("Accept")).thenReturn("application/xml");
		when(acceptHeaderToFormat.getFormat("application/xml")).thenReturn("xml");

		String format = resolver.getAcceptFormat();
		assertThat(format, is("xml"));

		verify(request).getHeader("Accept");
	}

	@Test
	public void if_formatNotSpecifiedAndNoAcceptsHaveFormat() {
		when(request.getParameter("_format")).thenReturn(null);
		when(request.getHeader("Accept")).thenReturn("application/SOMETHING_I_DONT_HAVE");

		String format = resolver.getAcceptFormat();
		assertNull(format);

		verify(request).getHeader("Accept");
	}

	@Test
	public void ifAcceptHeaderIsNullShouldReturnDefault() {
		when(request.getParameter("_format")).thenReturn(null);
		when(request.getHeader("Accept")).thenReturn(null);
		when(acceptHeaderToFormat.getFormat(null)).thenReturn("html");

		String format = resolver.getAcceptFormat();
		assertThat(format, is("html"));

	}

}
