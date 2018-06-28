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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PrimitiveByteConverterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private PrimitiveByteConverter converter;

	@Before
	public void setup() {
		this.converter = new PrimitiveByteConverter();
	}

	@Test
	public void shouldBeAbleToConvertNumbers() {
		assertThat(converter.convert("7", byte.class), is(equalTo((byte) 7)));
	}

	@Test
	public void shouldComplainAboutInvalidNumber() {
		exception.expect(hasConversionException("--- is not a valid number."));
		converter.convert("---", byte.class);
	}

	@Test
	public void shouldConvertToZeroWhenNull() {
		assertThat(converter.convert(null, byte.class), is(equalTo((byte) 0)));
	}

	@Test
	public void shouldConvertToZeroWhenEmpty() {
		assertThat(converter.convert("", byte.class), is(equalTo((byte) 0)));
	}
}
