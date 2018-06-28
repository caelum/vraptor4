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
import static org.hamcrest.Matchers.nullValue;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * VRaptor's BigInteger converter test.
 *
 * @author Cecilia Fernandes
 */
public class BigIntegerConverterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private BigIntegerConverter converter;

	@Before
	public void setup() {
		this.converter = new BigIntegerConverter();
	}

	@Test
	public void shouldBeAbleToConvertIntegerNumbers() {
		assertThat(converter.convert("3", BigInteger.class), is(equalTo(new BigInteger("3"))));
	}

	@Test
	public void shouldComplainAboutNonIntegerNumbers() {
		exception.expect(hasConversionException("2.3 is not a valid number."));
		converter.convert("2.3", BigInteger.class);
	}

	@Test
	public void shouldComplainAboutInvalidNumber() {
		exception.expect(hasConversionException("--- is not a valid number."));
		converter.convert("---", BigInteger.class);
	}

	@Test
	public void shouldNotComplainAboutNull() {
		assertThat(converter.convert(null, BigInteger.class), is(nullValue()));
	}

	@Test
	public void shouldNotComplainAboutEmpty() {
		assertThat(converter.convert("", BigInteger.class), is(nullValue()));
	}
}
