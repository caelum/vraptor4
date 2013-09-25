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
import static org.mockito.Mockito.when;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.http.MutableRequest;

public class LocaleBasedTimeConverterTest {

    static final String LOCALE_KEY = "javax.servlet.jsp.jstl.fmt.locale";

    private LocaleBasedTimeConverter converter;
    private @Mock MutableRequest request;
    private @Mock HttpSession session;
    private @Mock ServletContext context;
    private @Mock ResourceBundle bundle;

	@Before
	public void setup() {
        MockitoAnnotations.initMocks(this);

        when(request.getServletContext()).thenReturn(context);

        bundle = ResourceBundle.getBundle("messages");
        converter = new LocaleBasedTimeConverter(new Locale("pt", "BR"), bundle);
        Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void shouldBeAbleToConvert() throws ParseException {
		Date date = new SimpleDateFormat("HH:mm:ss").parse("23:52:00");
		assertThat(converter.convert("23:52", Time.class), is(equalTo(date)));
		assertThat(converter.convert("23:52:00", Time.class), is(equalTo(date)));
	}

	@Test
	public void shouldBeAbleToConvertEmpty() {
		assertThat(converter.convert("", Time.class), is(nullValue()));
	}

	@Test
	public void shouldBeAbleToConvertNull() {
		assertThat(converter.convert(null, Time.class), is(nullValue()));
	}

	@Test
	public void shouldThrowExceptionWhenUnableToParse() {
		try {
			converter.convert("25:dd:88", Time.class);
		} catch (ConversionException e) {
			assertThat(e.getMessage(), is(equalTo("25:dd:88 is not a valid time.")));
		}
	}
}
