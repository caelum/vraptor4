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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptorMatchers;


public class PrimitiveShortConverterTest {

	private PrimitiveShortConverter converter;

	@Before
	public void setup() {
		this.converter = new PrimitiveShortConverter();
	}

	@Test
	public void shouldBeAbleToConvertNumbers(){
		assertThat(converter.convert("5", short.class), is(equalTo((short) 5)));
	}

	@Test
	public void shouldComplainAboutInvalidNumber() {
		try {
			converter.convert("---", short.class);
		} catch (ConversionException e) {
			assertThat(e.getValidationMessage(), VRaptorMatchers.hasMessage("--- is not a valid integer."));
		}
	}

	@Test
	public void shouldConvertToZeroWhenNull() {
		assertThat(converter.convert(null, short.class), is(equalTo((short) 0)));
	}

	@Test
	public void shouldConvertToZeroWhenEmpty() {
		assertThat(converter.convert("", short.class), is(equalTo((short) 0)));
	}

}
