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

public class PrimitiveBooleanConverterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private PrimitiveBooleanConverter converter;

	@Before
	public void setup() {
		this.converter = new PrimitiveBooleanConverter(new BooleanConverter());
	}

	@Test
	public void shouldBeAbleToConvertNumbers() {
		assertThat(converter.convert("", boolean.class), is(equalTo(false)));
		assertThat(converter.convert("false", boolean.class), is(equalTo(false)));
		assertThat(converter.convert("true", boolean.class), is(equalTo(true)));
		assertThat(converter.convert("True", boolean.class), is(equalTo(true)));
	}

	@Test
	public void shouldConvertToZeroWhenNull() {
		assertThat(converter.convert(null, boolean.class), is(equalTo(false)));
	}

	@Test
	public void shouldConvertToZeroWhenEmpty() {
		assertThat(converter.convert("", boolean.class), is(equalTo(false)));
	}

	@Test
	public void shouldConvertYesNo() {
		assertThat(converter.convert("yes", boolean.class), is(equalTo(true)));
		assertThat(converter.convert("no", boolean.class), is(equalTo(false)));
	}

	@Test
	public void shouldConvertYN() {
		assertThat(converter.convert("y", boolean.class), is(equalTo(true)));
		assertThat(converter.convert("n", boolean.class), is(equalTo(false)));
	}

	@Test
	public void shouldConvertOnOff() {
		assertThat(converter.convert("on", boolean.class), is(equalTo(true)));
		assertThat(converter.convert("off", boolean.class), is(equalTo(false)));
	}

	@Test
	public void shouldConvertIgnoringCase() {
		assertThat(converter.convert("truE", boolean.class), is(equalTo(true)));
		assertThat(converter.convert("FALSE", boolean.class), is(equalTo(false)));
		assertThat(converter.convert("On", boolean.class), is(equalTo(true)));
		assertThat(converter.convert("oFf", boolean.class), is(equalTo(false)));
	}

	@Test
	public void shouldThrowExceptionForInvalidString() {
		exception.expect(hasConversionException("NOT A BOOLEAN! is not a valid boolean. Please use true/false, yes/no, y/n or on/off"));
		converter.convert("not a boolean!", boolean.class);
	}
}
