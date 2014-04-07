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

import static br.com.caelum.vraptor.http.EncodingHandler.ENCODING_KEY;
import static java.nio.charset.Charset.defaultCharset;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

public class EncodingHandlerTest {

	private ServletContext context;

	@Before
	public void setUp() throws Exception {
		context = mock(ServletContext.class);
	}

	@Test
	public void shouldReturnDefaultCharsetWhenThereIsNoEncodingInitParameter() throws Exception {
		when(context.getInitParameter(ENCODING_KEY)).thenReturn(null);

		EncodingHandler encodingHandler = new EncodingHandler(context);
		encodingHandler.init();
		
		assertThat(encodingHandler.getEncoding(), is(defaultCharset().name()));
	}
	
	@Test
	public void shouldReturnWebxmlValueWhenThereIsAnEncodingInitParameter() throws Exception {
		when(context.getInitParameter(ENCODING_KEY)).thenReturn("ISO-8859-1");
		
		EncodingHandler encodingHandler = new EncodingHandler(context);
		encodingHandler.init();
		
		assertThat(encodingHandler.getEncoding(), is("ISO-8859-1"));
	}
}
