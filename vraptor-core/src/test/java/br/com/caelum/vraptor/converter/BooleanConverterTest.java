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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class BooleanConverterTest {

	private BooleanConverter converter;

	@Before
	public void setup() {
		this.converter = new BooleanConverter();
	}

	@Test
	public void shouldBeAbleToConvertTrueAndFalse(){
		assertThat(converter.convert("true", Boolean.class), is(equalTo(true)));
		assertThat(converter.convert("false", Boolean.class), is(equalTo(false)));
	}

	@Test
	public void shouldConvertEmptyToNull() {
		assertThat(converter.convert("", Boolean.class), is(nullValue()));
	}

	@Test
	public void shouldNotComplainAboutNull() {
		assertThat(converter.convert(null, Boolean.class), is(nullValue()));
	}

	@Test
	public void shouldConvertYesNo() {
		assertThat(converter.convert("yes", Boolean.class), is(equalTo(true)));
		assertThat(converter.convert("no", Boolean.class), is(equalTo(false)));
	}
	@Test
	public void shouldConvertYN() {
		assertThat(converter.convert("y", Boolean.class), is(equalTo(true)));
		assertThat(converter.convert("n", Boolean.class), is(equalTo(false)));
	}
	@Test
	public void shouldConvertOnOff() {
		assertThat(converter.convert("on", Boolean.class), is(equalTo(true)));
		assertThat(converter.convert("off", Boolean.class), is(equalTo(false)));
	}

	@Test
	public void shouldConvertIgnoringCase() {
		assertThat(converter.convert("truE", Boolean.class), is(equalTo(true)));
		assertThat(converter.convert("FALSE", Boolean.class), is(equalTo(false)));
		assertThat(converter.convert("On", Boolean.class), is(equalTo(true)));
		assertThat(converter.convert("oFf", Boolean.class), is(equalTo(false)));
	}

	@Test
	public void shouldThrowExceptionForInvalidString() {
		try {
			converter.convert("not a boolean!", Boolean.class);
			Assert.assertTrue(false);
		} catch(ConversionException e) {
			assertThat(e.getValidationMessage(), hasMessage("NOT A BOOLEAN! is not a valid boolean. Please use true/false, yes/no, y/n or on/off"));
		}
	}
}
