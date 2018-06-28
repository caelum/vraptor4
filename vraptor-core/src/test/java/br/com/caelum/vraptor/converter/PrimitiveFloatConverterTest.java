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

package br.com.caelum.vraptor.converter;
import static br.com.caelum.vraptor.VRaptorMatchers.hasConversionException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.http.MutableRequest;

public class PrimitiveFloatConverterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private PrimitiveFloatConverter converter;
	private @Mock MutableRequest request;
	private @Mock HttpSession session;
	private @Mock ServletContext context;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(request.getServletContext()).thenReturn(context);

		converter = new PrimitiveFloatConverter(new Locale("pt", "BR"));
	}

	@Test
	public void shouldBeAbleToConvertWithPTBR() {
		assertThat(converter.convert("10,00", float.class), is(equalTo(Float.parseFloat("10.00"))));
		assertThat(converter.convert("10,01", float.class), is(equalTo(Float.parseFloat("10.01"))));
	}

	@Test
	public void shouldBeAbleToConvertWithENUS() {
		converter = new PrimitiveFloatConverter(new Locale("en", "US"));
		assertThat(converter.convert("10.00", float.class), is(equalTo(Float.parseFloat("10.00"))));
		assertThat(converter.convert("10.01", float.class), is(equalTo(Float.parseFloat("10.01"))));
	}

	@Test
	public void shouldBeAbleToConvertEmpty() {
		assertThat(converter.convert("", float.class), is(equalTo(0f)));
	}

	@Test
	public void shouldBeAbleToConvertNull() {
		assertThat(converter.convert(null, float.class), is(equalTo(0f)));
	}

	@Test
	public void shouldThrowExceptionWhenUnableToParse() {
		exception.expect(hasConversionException("vr3.9 is not a valid number."));
		converter.convert("vr3.9", float.class);
	}
}
