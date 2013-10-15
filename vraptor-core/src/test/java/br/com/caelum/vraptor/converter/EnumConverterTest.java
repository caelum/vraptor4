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

import static br.com.caelum.vraptor.VRaptorMatchers.hasMessage;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;

public class EnumConverterTest {

	private Converter<Enum> converter;

	@Before
	public void setup() {
		this.converter = new EnumConverter();
	}

	@Test
	public void shouldBeAbleToConvertByOrdinal() {
		Enum value = converter.convert("1", MyCustomEnum.class);
		MyCustomEnum second = MyCustomEnum.SECOND;
		assertEquals(value, second);
	}

	@Test
	public void shouldBeAbleToConvertByName() {

		Enum value = converter.convert("FIRST", MyCustomEnum.class);
		MyCustomEnum first = MyCustomEnum.FIRST;
		assertEquals(value, first);
	}

	@Test
	public void shouldConvertEmptyToNull() {
		assertThat(converter.convert("", MyCustomEnum.class), is(nullValue()));
	}

	@Test
	public void shouldComplainAboutInvalidIndex() {
		try {
			converter.convert("3200", MyCustomEnum.class);
		} catch (ConversionException e) {
			assertThat(e.getValidationMessage(), hasMessage("3200 is not a valid option."));
		}
	}

	@Test
	public void shouldComplainAboutInvalidNumber() {
		try {
			converter.convert("32a00", MyCustomEnum.class);
		} catch (ConversionException e) {
			assertThat(e.getValidationMessage(), hasMessage("32a00 is not a valid option."));
		}
	}

	@Test
	public void shouldComplainAboutInvalidOrdinal() {
		try {
			converter.convert("THIRD", MyCustomEnum.class);
		} catch (ConversionException e) {
			assertThat(e.getValidationMessage(), hasMessage("THIRD is not a valid option."));
		}
	}

	@Test
	public void shouldAcceptNull() {
		assertThat(converter.convert(null, MyCustomEnum.class), is(nullValue()));
	}

	enum MyCustomEnum {
		FIRST, SECOND
	}
}
