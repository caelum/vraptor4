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
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;


/**
 * VRaptor's BigDecimal converter test.
 *
 * @author Cecilia Fernandes
 */
public class BigDecimalConverterTest {

	private BigDecimalConverter converter;
	private ResourceBundle bundle;

	@Before
	public void setup() {
		bundle = ResourceBundle.getBundle("messages");
        converter = new BigDecimalConverter(bundle);
	}

	@Test
	public void shouldBeAbleToConvertIntegerNumbers() {
		assertThat(converter.convert("2.3", BigDecimal.class), is(equalTo(new BigDecimal("2.3"))));
	}

	@Test
	public void shouldComplainAboutInvalidNumber() {
		try {
			converter.convert("---", BigDecimal.class);
		} catch (ConversionException e) {
			assertThat(e.getMessage(), is(equalTo("--- is not a valid number.")));
		}
	}

	@Test
	public void shouldNotComplainAboutNull() {
		assertThat(converter.convert(null, BigDecimal.class), is(nullValue()));
	}

	@Test
	public void shouldNotComplainAboutEmpty() {
		assertThat(converter.convert("", BigDecimal.class), is(nullValue()));
	}

}
